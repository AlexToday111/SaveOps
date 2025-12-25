package com.saveops.goal.grpc;

import com.saveops.contracts.goal.CreateGoalRequest;
import com.saveops.contracts.goal.GetGoalProgressRequest;
import com.saveops.contracts.goal.GetGoalRequest;
import com.saveops.contracts.goal.GoalProgressResponse;
import com.saveops.contracts.goal.GoalResponse;
import com.saveops.contracts.goal.GoalServiceGrpc;
import com.saveops.goal.service.GoalApplicationService;
import com.saveops.goal.service.GoalProgressResult;
import com.saveops.goal.service.GoalResult;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.UUID;

@GrpcService
public class GoalGrpcService extends GoalServiceGrpc.GoalServiceImplBase {
    private final GoalApplicationService goalService;

    public GoalGrpcService(GoalApplicationService goalService) {
        this.goalService = goalService;
    }

    @Override
    public void createGoal(CreateGoalRequest request, StreamObserver<GoalResponse> responseObserver) {
        handle(responseObserver, () -> toResponse(goalService.createGoal(
                request.getOwnerId(),
                UUID.fromString(request.getAccountId()),
                request.getName(),
                new BigDecimal(request.getTargetAmount()),
                request.getCurrency()
        )));
    }

    @Override
    public void getGoal(GetGoalRequest request, StreamObserver<GoalResponse> responseObserver) {
        handle(responseObserver, () -> toResponse(goalService.getGoal(UUID.fromString(request.getGoalId()))));
    }

    @Override
    public void getGoalProgress(GetGoalProgressRequest request, StreamObserver<GoalProgressResponse> responseObserver) {
        handle(responseObserver, () -> toResponse(goalService.getProgress(UUID.fromString(request.getGoalId()))));
    }

    private GoalResponse toResponse(GoalResult goal) {
        return GoalResponse.newBuilder()
                .setGoalId(goal.goalId().toString())
                .setOwnerId(goal.ownerId())
                .setAccountId(goal.accountId().toString())
                .setName(goal.name())
                .setTargetAmount(goal.targetAmount().toPlainString())
                .setCurrency(goal.currency())
                .build();
    }

    private GoalProgressResponse toResponse(GoalProgressResult progress) {
        return GoalProgressResponse.newBuilder()
                .setGoalId(progress.goalId().toString())
                .setAccountId(progress.accountId().toString())
                .setCurrentAmount(progress.currentAmount().toPlainString())
                .setTargetAmount(progress.targetAmount().toPlainString())
                .setProgressPercent(progress.progressPercent())
                .build();
    }

    private <T> void handle(StreamObserver<T> observer, GrpcCall<T> call) {
        try {
            observer.onNext(call.execute());
            observer.onCompleted();
        } catch (IllegalArgumentException ex) {
            observer.onError(Status.INVALID_ARGUMENT.withDescription(ex.getMessage()).asRuntimeException());
        } catch (Exception ex) {
            observer.onError(Status.INTERNAL.withDescription("Internal goal service error").asRuntimeException());
        }
    }

    @FunctionalInterface
    private interface GrpcCall<T> {
        T execute();
    }
}

