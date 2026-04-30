package com.saveops.gateway.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public final class AccountDtos {
    private AccountDtos() {
    }

    public record CreateAccountRequest(@NotBlank String ownerId, @NotBlank String currency) {
    }

    public record MoneyRequest(@NotNull @DecimalMin("0.01") BigDecimal amount, String operationId) {
    }

    public record AccountResponse(String accountId, String ownerId, String status, String currency, String balance) {
    }

    public record BalanceResponse(String accountId, String currency, String balance) {
    }

    public record MoneyResponse(String accountId, String currency, String balance, String ledgerEntryId) {
    }
}

