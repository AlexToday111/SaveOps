package com.saveops.gateway.grpc;

import com.saveops.common.logging.CorrelationId;
import com.saveops.contracts.account.AccountResponse;
import com.saveops.contracts.account.AccountServiceGrpc;
import com.saveops.contracts.account.BalanceResponse;
import com.saveops.contracts.account.CreateAccountRequest;
import com.saveops.contracts.account.GetBalanceRequest;
import com.saveops.contracts.account.MoneyOperationRequest;
import com.saveops.contracts.account.MoneyOperationResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class AccountGrpcClient {
    private static final Duration DEADLINE = Duration.ofSeconds(2);

    @GrpcClient("account-service")
    private AccountServiceGrpc.AccountServiceBlockingStub accountStub;

    public AccountResponse createAccount(String ownerId, String currency) {
        return withDeadline().createAccount(CreateAccountRequest.newBuilder()
                .setOwnerId(ownerId)
                .setCurrency(currency)
                .setCorrelationId(CorrelationId.currentOrNew())
                .build());
    }

    public BalanceResponse getBalance(String accountId) {
        return withDeadline().getBalance(GetBalanceRequest.newBuilder().setAccountId(accountId).build());
    }

    public MoneyOperationResponse deposit(String accountId, BigDecimal amount, String operationId) {
        return withDeadline().depositMoney(moneyRequest(accountId, amount, operationId));
    }

    public MoneyOperationResponse withdraw(String accountId, BigDecimal amount, String operationId) {
        return withDeadline().withdrawMoney(moneyRequest(accountId, amount, operationId));
    }

    private MoneyOperationRequest moneyRequest(String accountId, BigDecimal amount, String operationId) {
        String key = operationId == null || operationId.isBlank() ? UUID.randomUUID().toString() : operationId;
        return MoneyOperationRequest.newBuilder()
                .setAccountId(accountId)
                .setAmount(amount.toPlainString())
                .setOperationId(key)
                .setCorrelationId(CorrelationId.currentOrNew())
                .build();
    }

    private AccountServiceGrpc.AccountServiceBlockingStub withDeadline() {
        return accountStub.withDeadlineAfter(DEADLINE.toMillis(), TimeUnit.MILLISECONDS);
    }
}

