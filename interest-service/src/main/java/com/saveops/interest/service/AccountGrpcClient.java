package com.saveops.interest.service;

import com.saveops.common.logging.CorrelationId;
import com.saveops.contracts.account.AccountServiceGrpc;
import com.saveops.contracts.account.GetBalanceRequest;
import com.saveops.contracts.account.MoneyOperationRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class AccountGrpcClient {
    private static final Duration DEADLINE = Duration.ofSeconds(3);

    @GrpcClient("account-service")
    private AccountServiceGrpc.AccountServiceBlockingStub accountStub;

    public BigDecimal getBalance(UUID accountId) {
        return new BigDecimal(accountStub.withDeadlineAfter(DEADLINE.toMillis(), TimeUnit.MILLISECONDS)
                .getBalance(GetBalanceRequest.newBuilder().setAccountId(accountId.toString()).build())
                .getBalance());
    }

    public void depositInterest(UUID accountId, BigDecimal amount, String operationId) {
        accountStub.withDeadlineAfter(DEADLINE.toMillis(), TimeUnit.MILLISECONDS)
                .depositMoney(MoneyOperationRequest.newBuilder()
                        .setAccountId(accountId.toString())
                        .setAmount(amount.toPlainString())
                        .setOperationId(operationId)
                        .setCorrelationId(CorrelationId.currentOrNew())
                        .build());
    }
}

