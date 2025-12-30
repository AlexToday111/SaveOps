package com.saveops.account.service;

import com.saveops.account.entity.AccountEntity;
import com.saveops.account.entity.AccountStatus;
import com.saveops.account.entity.LedgerEntryEntity;
import com.saveops.account.entity.LedgerEntryType;
import com.saveops.account.messaging.AccountEventPublisher;
import com.saveops.account.repository.AccountRepository;
import com.saveops.account.repository.LedgerEntryRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AccountApplicationService {
    private static final Duration IDEMPOTENCY_TTL = Duration.ofHours(24);
    private static final Duration BALANCE_CACHE_TTL = Duration.ofMinutes(5);

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final AccountEventPublisher eventPublisher;
    private final StringRedisTemplate redisTemplate;
    private final Counter accountsCreated;
    private final Counter moneyTransfers;

    public AccountApplicationService(AccountRepository accountRepository,
                                     LedgerEntryRepository ledgerEntryRepository,
                                     AccountEventPublisher eventPublisher,
                                     StringRedisTemplate redisTemplate,
                                     MeterRegistry meterRegistry) {
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.eventPublisher = eventPublisher;
        this.redisTemplate = redisTemplate;
        this.accountsCreated = Counter.builder("saveops_accounts_created_total").register(meterRegistry);
        this.moneyTransfers = Counter.builder("saveops_money_transfers_total").register(meterRegistry);
    }

    @Transactional
    public AccountResult createAccount(String ownerId, String currency, String correlationId) {
        AccountEntity account = new AccountEntity(UUID.randomUUID(), ownerId, currency, BigDecimal.ZERO, AccountStatus.ACTIVE, Instant.now());
        accountRepository.save(account);
        accountsCreated.increment();
        eventPublisher.accountOpened(account.getId().toString(), correlationId, Map.of(
                "ownerId", ownerId,
                "currency", currency
        ));
        cacheBalance(account);
        return toResult(account);
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID accountId) {
        String cacheKey = balanceKey(accountId);
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return new BigDecimal(cached);
        }
        AccountEntity account = findAccount(accountId);
        redisTemplate.opsForValue().set(cacheKey, account.getBalance().toPlainString(), BALANCE_CACHE_TTL);
        return account.getBalance();
    }

    @Transactional
    public MoneyOperationResult deposit(UUID accountId, BigDecimal amount, String operationId, String correlationId) {
        return applyMoney(accountId, amount, operationId, correlationId, LedgerEntryType.DEPOSIT);
    }

    @Transactional
    public MoneyOperationResult roundUp(UUID accountId, BigDecimal amount, String operationId, String correlationId) {
        return applyMoney(accountId, amount, operationId, correlationId, LedgerEntryType.ROUND_UP);
    }

    @Transactional
    public MoneyOperationResult withdraw(UUID accountId, BigDecimal amount, String operationId, String correlationId) {
        return applyMoney(accountId, amount, operationId, correlationId, LedgerEntryType.WITHDRAW);
    }

    @Transactional
    public AccountResult close(UUID accountId, String correlationId) {
        AccountEntity account = findAccount(accountId);
        account.close();
        eventPublisher.accountClosed(account.getId().toString(), correlationId, Map.of("ownerId", account.getOwnerId()));
        cacheBalance(account);
        return toResult(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResult> listAccounts(String ownerId) {
        return accountRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId).stream().map(this::toResult).toList();
    }

    private MoneyOperationResult applyMoney(UUID accountId, BigDecimal amount, String operationId, String correlationId, LedgerEntryType type) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        String idempotencyKey = RedisKeyFactory.idempotencyKey(operationId);
        Boolean created = redisTemplate.opsForValue().setIfAbsent(idempotencyKey, accountId.toString(), IDEMPOTENCY_TTL);
        if (Boolean.FALSE.equals(created)) {
            return ledgerEntryRepository.findByOperationId(operationId)
                    .map(entry -> new MoneyOperationResult(accountId, findAccount(accountId).getCurrency(), findAccount(accountId).getBalance(), entry.getId()))
                    .orElseGet(() -> {
                        AccountEntity account = findAccount(accountId);
                        return new MoneyOperationResult(accountId, account.getCurrency(), account.getBalance(), UUID.nameUUIDFromBytes(operationId.getBytes()));
                    });
        }

        AccountEntity account = findAccount(accountId);
        if (type == LedgerEntryType.DEPOSIT || type == LedgerEntryType.INTEREST || type == LedgerEntryType.ROUND_UP) {
            account.deposit(amount);
        } else {
            account.withdraw(amount);
        }
        LedgerEntryEntity entry = new LedgerEntryEntity(UUID.randomUUID(), account, type, amount, account.getBalance(), operationId, correlationId, Instant.now());
        ledgerEntryRepository.save(entry);
        cacheBalance(account);
        moneyTransfers.increment();
        eventPublisher.moneyTransferred(account.getId().toString(), correlationId, Map.of(
                "type", type.name(),
                "amount", amount.toPlainString(),
                "currency", account.getCurrency(),
                "balanceAfter", account.getBalance().toPlainString()
        ));
        return new MoneyOperationResult(account.getId(), account.getCurrency(), account.getBalance(), entry.getId());
    }

    private AccountEntity findAccount(UUID accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    private AccountResult toResult(AccountEntity account) {
        return new AccountResult(account.getId(), account.getOwnerId(), account.getStatus().name(), account.getCurrency(), account.getBalance());
    }

    private void cacheBalance(AccountEntity account) {
        redisTemplate.opsForValue().set(balanceKey(account.getId()), account.getBalance().toPlainString(), BALANCE_CACHE_TTL);
    }

    private String balanceKey(UUID accountId) {
        return "account:balance:" + accountId;
    }
}
