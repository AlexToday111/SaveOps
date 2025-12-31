package com.saveops.interest.repository;

import com.saveops.interest.entity.TrackedAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrackedAccountRepository extends JpaRepository<TrackedAccountEntity, UUID> {
}

