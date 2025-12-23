package com.saveops.account.repository;

import com.saveops.account.entity.LedgerEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntryEntity, UUID> {
    Optional<LedgerEntryEntity> findByOperationId(String operationId);
}

