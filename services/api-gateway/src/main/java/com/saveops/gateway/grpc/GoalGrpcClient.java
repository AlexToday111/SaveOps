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
import java.util.function.Supplier;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

@Component
public class GoalGrpcClient {
    private static final Duration DEADLINE = Duration.ofSeconds(2);

    @GrpcClient("goal-service")
    private GoalServiceGrpc.GoalServiceBlockingStub goalStub;

    public GoalResponse createGoal(String ownerId, String accountId, String name, BigDecimal targetAmount, String currency) {
        return retry(() -> withDeadline().createGoal(CreateGoalRequest.newBuilder()
                .setOwnerId(ownerId)
                .setAccountId(accountId)
                .setName(name)
                .setTargetAmount(targetAmount.toPlainString())
                .setCurrency(currency)
                .setCorrelationId(CorrelationId.currentOrNew())
                .build()));
    }

    public GoalResponse getGoal(String goalId) {
        return retry(() -> withDeadline().getGoal(GetGoalRequest.newBuilder().setGoalId(goalId).build()));
    }

    public GoalProgressResponse getProgress(String goalId) {
        return retry(() -> withDeadline().getGoalProgress(GetGoalProgressRequest.newBuilder().setGoalId(goalId).build()));
    }

    private GoalServiceGrpc.GoalServiceBlockingStub withDeadline() {
        return goalStub.withDeadlineAfter(DEADLINE.toMillis(), TimeUnit.MILLISECONDS);
    }

    private <T> T retry(Supplier<T> call) {
        StatusRuntimeException last = null;
        for (int attempt = 0; attempt < 2; attempt++) {
            try {
                return call.get();
            } catch (StatusRuntimeException ex) {
                last = ex;
                if (ex.getStatus().getCode() != Status.Code.UNAVAILABLE
                        && ex.getStatus().getCode() != Status.Code.DEADLINE_EXCEEDED) {
                    throw ex;
                }
            }
        }
        throw last;
    }
}
