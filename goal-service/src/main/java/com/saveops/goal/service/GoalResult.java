package com.saveops.goal.service;

import java.math.BigDecimal;
import java.util.UUID;

public record GoalResult(UUID goalId, String ownerId, UUID accountId, String name, BigDecimal targetAmount, String currency) {
}

