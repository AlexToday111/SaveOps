package com.saveops.goal.service;

import com.saveops.goal.entity.SavingsGoalEntity;
import com.saveops.goal.grpc.AccountBalanceClient;
import com.saveops.goal.repository.SavingsGoalRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GoalApplicationServiceTest {
    @Test
    void progressIsCalculatedFromAccountBalanceAndTargetAmount() {
        UUID goalId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        SavingsGoalEntity goal = new SavingsGoalEntity(goalId, "user-1", accountId, "Vacation", new BigDecimal("1000.00"), "RUB", Instant.now());
        SavingsGoalRepository repository = mock(SavingsGoalRepository.class);
        AccountBalanceClient balanceClient = mock(AccountBalanceClient.class);
        when(repository.findById(goalId)).thenReturn(Optional.of(goal));
        when(balanceClient.getBalance(accountId.toString())).thenReturn(new BigDecimal("250.00"));
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
        SavingsGoalRepository repository = mock(SavingsGoalRepository.class);
        AccountBalanceClient balanceClient = mock(AccountBalanceClient.class);
        when(repository.findById(goalId)).thenReturn(Optional.of(goal));
        when(balanceClient.getBalance(accountId.toString())).thenReturn(new BigDecimal("1200.00"));
        GoalApplicationService service = new GoalApplicationService(repository, balanceClient);

        GoalProgressResult progress = service.getProgress(goalId);

        assertThat(progress.progressPercent()).isEqualTo(100.0);
    }
}
