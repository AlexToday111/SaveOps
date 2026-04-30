package com.saveops.goal.service;

import com.saveops.goal.entity.SavingsGoalEntity;
import com.saveops.goal.grpc.AccountBalanceClient;
import com.saveops.goal.repository.SavingsGoalRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GoalApplicationServiceTest {
    @Test
    void progressIsCalculatedFromAccountBalanceAndTargetAmount() {
        UUID goalId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        SavingsGoalEntity goal = new SavingsGoalEntity(goalId, "user-1", accountId, "Vacation", new BigDecimal("1000.00"), "RUB", Instant.now());
        SavingsGoalRepository repository = repositoryReturning(goal);
        AccountBalanceClient balanceClient = balanceClientReturning(new BigDecimal("250.00"));
        GoalApplicationService service = new GoalApplicationService(repository, balanceClient);

        GoalProgressResult progress = service.getProgress(goalId);

        assertThat(progress.currentAmount()).isEqualByComparingTo("250.00");
        assertThat(progress.progressPercent()).isEqualTo(25.0);
    }

    @Test
    void progressIsCappedAtOneHundredPercent() {
        UUID goalId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        SavingsGoalEntity goal = new SavingsGoalEntity(goalId, "user-1", accountId, "Laptop", new BigDecimal("1000.00"), "RUB", Instant.now());
        SavingsGoalRepository repository = repositoryReturning(goal);
        AccountBalanceClient balanceClient = balanceClientReturning(new BigDecimal("1200.00"));
        GoalApplicationService service = new GoalApplicationService(repository, balanceClient);

        GoalProgressResult progress = service.getProgress(goalId);

        assertThat(progress.progressPercent()).isEqualTo(100.0);
    }

    private SavingsGoalRepository repositoryReturning(SavingsGoalEntity goal) {
        return (SavingsGoalRepository) Proxy.newProxyInstance(
                SavingsGoalRepository.class.getClassLoader(),
                new Class<?>[]{SavingsGoalRepository.class},
                (proxy, method, args) -> {
                    if ("findById".equals(method.getName())) {
                        return Optional.of(goal);
                    }
                    throw new UnsupportedOperationException("Method is not needed by this unit test: " + method.getName());
                }
        );
    }

    private AccountBalanceClient balanceClientReturning(BigDecimal balance) {
        return new AccountBalanceClient() {
            @Override
            public BigDecimal getBalance(String accountId) {
                return balance;
            }
        };
    }
}
