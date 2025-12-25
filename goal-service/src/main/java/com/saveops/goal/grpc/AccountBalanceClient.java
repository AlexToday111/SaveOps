package com.saveops.goal.grpc;

import com.saveops.contracts.account.AccountServiceGrpc;
import com.saveops.contracts.account.GetBalanceRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class AccountBalanceClient {
    private static final Duration DEADLINE = Duration.ofSeconds(2);

    @GrpcClient("account-service")
    private AccountServiceGrpc.AccountServiceBlockingStub accountStub;

    public BigDecimal getBalance(String accountId) {
        return new BigDecimal(accountStub
                .withDeadlineAfter(DEADLINE.toMillis(), TimeUnit.MILLISECONDS)
                .getBalance(GetBalanceRequest.newBuilder().setAccountId(accountId).build())
                .getBalance());
    }
}

