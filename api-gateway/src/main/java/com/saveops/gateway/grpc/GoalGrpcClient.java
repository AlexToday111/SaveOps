package com.saveops.gateway.grpc;

import com.saveops.common.logging.CorrelationId;
import com.saveops.contracts.goal.CreateGoalRequest;
import com.saveops.contracts.goal.GetGoalProgressRequest;
import com.saveops.contracts.goal.GetGoalRequest;
import com.saveops.contracts.goal.GoalProgressResponse;
import com.saveops.contracts.goal.GoalResponse;
import com.saveops.contracts.goal.GoalServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class GoalGrpcClient {
    private static final Duration DEADLINE = Duration.ofSeconds(2);

    @GrpcClient("goal-service")
    private GoalServiceGrpc.GoalServiceBlockingStub goalStub;

    public GoalResponse createGoal(String ownerId, String accountId, String name, BigDecimal targetAmount, String currency) {
        return withDeadline().createGoal(CreateGoalRequest.newBuilder()
                .setOwnerId(ownerId)
                .setAccountId(accountId)
                .setName(name)
                .setTargetAmount(targetAmount.toPlainString())
                .setCurrency(currency)
                .setCorrelationId(CorrelationId.currentOrNew())
                .build());
    }

    public GoalResponse getGoal(String goalId) {
        return withDeadline().getGoal(GetGoalRequest.newBuilder().setGoalId(goalId).build());
    }

    public GoalProgressResponse getProgress(String goalId) {
        return withDeadline().getGoalProgress(GetGoalProgressRequest.newBuilder().setGoalId(goalId).build());
    }

    private GoalServiceGrpc.GoalServiceBlockingStub withDeadline() {
        return goalStub.withDeadlineAfter(DEADLINE.toMillis(), TimeUnit.MILLISECONDS);
    }
}

