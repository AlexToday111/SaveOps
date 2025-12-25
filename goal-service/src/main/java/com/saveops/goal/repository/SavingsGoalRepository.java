package com.saveops.goal.repository;

import com.saveops.goal.entity.SavingsGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoalEntity, UUID> {
}

