package com.saveops.account.grpc;

import com.saveops.account.service.AccountApplicationService;
import com.saveops.account.service.AccountResult;
import com.saveops.account.service.MoneyOperationResult;
import com.saveops.contracts.account.AccountResponse;
import com.saveops.contracts.account.AccountServiceGrpc;
import com.saveops.contracts.account.BalanceResponse;
import com.saveops.contracts.account.CreateAccountRequest;
import com.saveops.contracts.account.GetBalanceRequest;
import com.saveops.contracts.account.ListAccountsRequest;
import com.saveops.contracts.account.ListAccountsResponse;
import com.saveops.contracts.account.MoneyOperationRequest;
import com.saveops.contracts.account.MoneyOperationResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.UUID;

@GrpcService
public class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {
    private final AccountApplicationService accountService;

    public AccountGrpcService(AccountApplicationService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void createAccount(CreateAccountRequest request, StreamObserver<AccountResponse> responseObserver) {
        handle(responseObserver, () -> toAccountResponse(accountService.createAccount(
                request.getOwnerId(), request.getCurrency(), request.getCorrelationId()
        )));
    }

    @Override
    public void getBalance(GetBalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        handle(responseObserver, () -> {
            UUID accountId = UUID.fromString(request.getAccountId());
            BigDecimal balance = accountService.getBalance(accountId);
            return BalanceResponse.newBuilder()
                    .setAccountId(accountId.toString())
                    .setCurrency("RUB")
                    .setBalance(balance.toPlainString())
                    .build();
        });
    }

    @Override
    public void depositMoney(MoneyOperationRequest request, StreamObserver<MoneyOperationResponse> responseObserver) {
        handle(responseObserver, () -> toMoneyResponse(accountService.deposit(
                UUID.fromString(request.getAccountId()),
                new BigDecimal(request.getAmount()),
                request.getOperationId(),
                request.getCorrelationId()
        )));
    }

    @Override
    public void withdrawMoney(MoneyOperationRequest request, StreamObserver<MoneyOperationResponse> responseObserver) {
        handle(responseObserver, () -> toMoneyResponse(accountService.withdraw(
                UUID.fromString(request.getAccountId()),
                new BigDecimal(request.getAmount()),
                request.getOperationId(),
                request.getCorrelationId()
        )));
    }

    @Override
    public void listAccounts(ListAccountsRequest request, StreamObserver<ListAccountsResponse> responseObserver) {
        handle(responseObserver, () -> ListAccountsResponse.newBuilder()
                .addAllAccounts(accountService.listAccounts(request.getOwnerId()).stream().map(this::toAccountResponse).toList())
                .build());
    }

    private AccountResponse toAccountResponse(AccountResult result) {
        return AccountResponse.newBuilder()
                .setAccountId(result.accountId().toString())
                .setOwnerId(result.ownerId())
                .setStatus(result.status())
                .setCurrency(result.currency())
                .setBalance(result.balance().toPlainString())
                .build();
    }

    private MoneyOperationResponse toMoneyResponse(MoneyOperationResult result) {
        return MoneyOperationResponse.newBuilder()
                .setAccountId(result.accountId().toString())
                .setCurrency(result.currency())
                .setBalance(result.balance().toPlainString())
                .setLedgerEntryId(result.ledgerEntryId().toString())
                .build();
    }

    private <T> void handle(StreamObserver<T> observer, GrpcCall<T> call) {
        try {
            observer.onNext(call.execute());
            observer.onCompleted();
        } catch (IllegalArgumentException ex) {
            observer.onError(Status.INVALID_ARGUMENT.withDescription(ex.getMessage()).asRuntimeException());
        } catch (IllegalStateException ex) {
            observer.onError(Status.FAILED_PRECONDITION.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception ex) {
            observer.onError(Status.INTERNAL.withDescription("Internal account service error").asRuntimeException());
        }
    }

    @FunctionalInterface
    private interface GrpcCall<T> {
        T execute();
    }
}

