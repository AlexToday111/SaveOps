package com.saveops.simulator.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@Component
public class RoundUpCalculator {
    private static final Set<Integer> SUPPORTED_STEPS = Set.of(10, 50, 100);

    public Result calculate(BigDecimal amount, int roundTo) {
        if (!SUPPORTED_STEPS.contains(roundTo)) {
            throw new IllegalArgumentException("roundTo must be 10, 50 or 100");
        }
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        BigDecimal step = BigDecimal.valueOf(roundTo);
        BigDecimal multiplier = amount.divide(step, 0, RoundingMode.CEILING);
        BigDecimal rounded = multiplier.multiply(step).setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundUp = rounded.subtract(amount).setScale(2, RoundingMode.HALF_UP);
        return new Result(rounded, roundUp);
    }

    public record Result(BigDecimal roundedAmount, BigDecimal roundUpAmount) {
    }
}

