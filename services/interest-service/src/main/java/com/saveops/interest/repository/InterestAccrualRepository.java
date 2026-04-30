package com.saveops.interest.repository;

import com.saveops.interest.entity.InterestAccrualEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface InterestAccrualRepository extends JpaRepository<InterestAccrualEntity, UUID> {
    Optional<InterestAccrualEntity> findByAccountIdAndAccrualDate(UUID accountId, LocalDate accrualDate);
}

