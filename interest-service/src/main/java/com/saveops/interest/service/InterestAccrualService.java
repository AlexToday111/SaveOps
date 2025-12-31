package com.saveops.interest.service;

import com.saveops.common.logging.CorrelationId;
import com.saveops.interest.entity.InterestAccrualEntity;
import com.saveops.interest.entity.TrackedAccountEntity;
import com.saveops.interest.messaging.InterestEventPublisher;
import com.saveops.interest.repository.InterestAccrualRepository;
import com.saveops.interest.repository.TrackedAccountRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
public class InterestAccrualService {
    private static final Logger log = LoggerFactory.getLogger(InterestAccrualService.class);
    private static final String LOCK_KEY = "interest:daily-accrual-lock";

    private final TrackedAccountRepository trackedAccountRepository;
    private final InterestAccrualRepository accrualRepository;
    private final StringRedisTemplate redisTemplate;
    private final AccountGrpcClient accountClient;
    private final InterestEventPublisher eventPublisher;
    private final BigDecimal annualRate;
    private final Counter accruedCounter;

    public InterestAccrualService(TrackedAccountRepository trackedAccountRepository,
                                  InterestAccrualRepository accrualRepository,
                                  StringRedisTemplate redisTemplate,
                                  AccountGrpcClient accountClient,
                                  InterestEventPublisher eventPublisher,
                                  @Value("${saveops.interest.annual-rate:0.06}") BigDecimal annualRate,
                                  MeterRegistry meterRegistry) {
        this.trackedAccountRepository = trackedAccountRepository;
        this.accrualRepository = accrualRepository;
        this.redisTemplate = redisTemplate;
        this.accountClient = accountClient;
        this.eventPublisher = eventPublisher;
        this.annualRate = annualRate;
        this.accruedCounter = Counter.builder("saveops_interest_accrued_total").register(meterRegistry);
    }

    @Scheduled(cron = "${saveops.interest.cron:0 0 3 * * *}")
    public void scheduledAccrual() {
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY + ":" + LocalDate.now(), "locked", Duration.ofHours(6));
        if (!Boolean.TRUE.equals(locked)) {
            log.info("Interest accrual skipped because distributed lock is held");
            return;
        }
        accrueForAllTrackedAccounts(LocalDate.now());
    }

    @Transactional
    public void accrueForAllTrackedAccounts(LocalDate accrualDate) {
        for (TrackedAccountEntity account : trackedAccountRepository.findAll()) {
            accrue(account, accrualDate);
        }
    }

    private void accrue(TrackedAccountEntity account, LocalDate accrualDate) {
        if (accrualRepository.findByAccountIdAndAccrualDate(account.getAccountId(), accrualDate).isPresent()) {
            return;
        }
        BigDecimal balance = accountClient.getBalance(account.getAccountId());
        BigDecimal amount = balance.multiply(annualRate).divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        if (amount.signum() <= 0) {
            return;
        }
        String operationId = "interest-" + accrualDate + "-" + account.getAccountId();
        accountClient.depositInterest(account.getAccountId(), amount, operationId);
        accrualRepository.save(new InterestAccrualEntity(UUID.randomUUID(), account.getAccountId(), accrualDate, amount, account.getCurrency(), Instant.now()));
        accruedCounter.increment();
        eventPublisher.interestAccrued(account.getAccountId().toString(), CorrelationId.currentOrNew(), Map.of(
                "accountId", account.getAccountId().toString(),
                "amount", amount.toPlainString(),
                "currency", account.getCurrency(),
                "accrualDate", accrualDate.toString()
        ));
    }
}

