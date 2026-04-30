package com.saveops.gateway.controller;

import com.saveops.gateway.dto.GoalDtos;
import com.saveops.gateway.grpc.GoalGrpcClient;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/goals")
public class GoalController {
    private final GoalGrpcClient goalClient;

    public GoalController(GoalGrpcClient goalClient) {
        this.goalClient = goalClient;
    }

    @PostMapping
    public GoalDtos.GoalResponse create(@Valid @RequestBody GoalDtos.CreateGoalRequest request) {
        var response = goalClient.createGoal(request.ownerId(), request.accountId(), request.name(), request.targetAmount(), request.currency());
        return toResponse(response);
    }

    @GetMapping("/{goalId}")
    public GoalDtos.GoalResponse get(@PathVariable String goalId) {
        return toResponse(goalClient.getGoal(goalId));
    }

    @GetMapping("/{goalId}/progress")
    public GoalDtos.GoalProgressResponse progress(@PathVariable String goalId) {
        var response = goalClient.getProgress(goalId);
        return new GoalDtos.GoalProgressResponse(response.getGoalId(), response.getAccountId(), response.getCurrentAmount(), response.getTargetAmount(), response.getProgressPercent());
    }

    private GoalDtos.GoalResponse toResponse(com.saveops.contracts.goal.GoalResponse response) {
        return new GoalDtos.GoalResponse(response.getGoalId(), response.getOwnerId(), response.getAccountId(), response.getName(), response.getTargetAmount(), response.getCurrency());
    }
}

