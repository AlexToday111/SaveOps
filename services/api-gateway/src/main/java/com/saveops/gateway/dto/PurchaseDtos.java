package com.saveops.gateway.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public final class PurchaseDtos {
    private PurchaseDtos() {
    }

    public record SimulatePurchaseRequest(
            @NotBlank String userId,
            @NotBlank String accountId,
            @NotNull @DecimalMin("0.01") BigDecimal amount,
            @NotNull Integer roundTo
    ) {
    }
}

