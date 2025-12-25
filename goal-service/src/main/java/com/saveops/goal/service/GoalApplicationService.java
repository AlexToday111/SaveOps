package com.saveops.goal.service;

import com.saveops.goal.entity.SavingsGoalEntity;
import com.saveops.goal.grpc.AccountBalanceClient;
import com.saveops.goal.repository.SavingsGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Service
public class GoalApplicationService {
    private final SavingsGoalRepository repository;
    private final AccountBalanceClient accountBalanceClient;

    public GoalApplicationService(SavingsGoalRepository repository, AccountBalanceClient accountBalanceClient) {
        this.repository = repository;
        this.accountBalanceClient = accountBalanceClient;
    }

    @Transactional
    public GoalResult createGoal(String ownerId, UUID accountId, String name, BigDecimal targetAmount, String currency) {
        if (targetAmount.signum() <= 0) {
            throw new IllegalArgumentException("Target amount must be positive");
        }
        SavingsGoalEntity goal = new SavingsGoalEntity(UUID.randomUUID(), ownerId, accountId, name, targetAmount, currency, Instant.now());
        repository.save(goal);
        return toResult(goal);
    }

    @Transactional(readOnly = true)
    public GoalResult getGoal(UUID goalId) {
        return toResult(findGoal(goalId));
    }

    @Transactional(readOnly = true)
    public GoalProgressResult getProgress(UUID goalId) {
        SavingsGoalEntity goal = findGoal(goalId);
        BigDecimal current = accountBalanceClient.getBalance(goal.getAccountId().toString());
        BigDecimal percent = current.multiply(BigDecimal.valueOf(100)).divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP);
        if (percent.compareTo(BigDecimal.valueOf(100)) > 0) {
            percent = BigDecimal.valueOf(100);
        }
        return new GoalProgressResult(goal.getId(), goal.getAccountId(), current, goal.getTargetAmount(), percent.doubleValue());
    }

    private SavingsGoalEntity findGoal(UUID goalId) {
        return repository.findById(goalId).orElseThrow(() -> new IllegalArgumentException("Goal not found"));
    }

    private GoalResult toResult(SavingsGoalEntity goal) {
        return new GoalResult(goal.getId(), goal.getOwnerId(), goal.getAccountId(), goal.getName(), goal.getTargetAmount(), goal.getCurrency());
    }
}

