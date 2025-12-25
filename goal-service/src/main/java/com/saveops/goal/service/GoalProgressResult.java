package com.saveops.goal.service;

import java.math.BigDecimal;
import java.util.UUID;

public record GoalProgressResult(UUID goalId, UUID accountId, BigDecimal currentAmount, BigDecimal targetAmount, double progressPercent) {
}

