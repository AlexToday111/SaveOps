package com.saveops.gateway.controller;

import com.saveops.gateway.dto.AccountDtos;
import com.saveops.gateway.grpc.AccountGrpcClient;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountGrpcClient accountClient;

    public AccountController(AccountGrpcClient accountClient) {
        this.accountClient = accountClient;
    }

    @PostMapping
    public AccountDtos.AccountResponse create(@Valid @RequestBody AccountDtos.CreateAccountRequest request) {
        var response = accountClient.createAccount(request.ownerId(), request.currency());
        return new AccountDtos.AccountResponse(response.getAccountId(), response.getOwnerId(), response.getStatus(), response.getCurrency(), response.getBalance());
    }

    @GetMapping("/{accountId}/balance")
    public AccountDtos.BalanceResponse balance(@PathVariable String accountId) {
        var response = accountClient.getBalance(accountId);
        return new AccountDtos.BalanceResponse(response.getAccountId(), response.getCurrency(), response.getBalance());
    }

    @PostMapping("/{accountId}/deposit")
    public AccountDtos.MoneyResponse deposit(@PathVariable String accountId, @Valid @RequestBody AccountDtos.MoneyRequest request) {
        var response = accountClient.deposit(accountId, request.amount(), request.operationId());
        return new AccountDtos.MoneyResponse(response.getAccountId(), response.getCurrency(), response.getBalance(), response.getLedgerEntryId());
    }

    @PostMapping("/{accountId}/withdraw")
    public AccountDtos.MoneyResponse withdraw(@PathVariable String accountId, @Valid @RequestBody AccountDtos.MoneyRequest request) {
        var response = accountClient.withdraw(accountId, request.amount(), request.operationId());
        return new AccountDtos.MoneyResponse(response.getAccountId(), response.getCurrency(), response.getBalance(), response.getLedgerEntryId());
    }
}

