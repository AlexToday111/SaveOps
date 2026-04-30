package com.saveops.gateway.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public final class GoalDtos {
    private GoalDtos() {
    }

    public record CreateGoalRequest(
            @NotBlank String ownerId,
            @NotBlank String accountId,
            @NotBlank String name,
            @NotNull @DecimalMin("0.01") BigDecimal targetAmount,
            @NotBlank String currency
    ) {
    }

    public record GoalResponse(String goalId, String ownerId, String accountId, String name, String targetAmount, String currency) {
    }

    public record GoalProgressResponse(String goalId, String accountId, String currentAmount, String targetAmount, double progressPercent) {
    }
}

