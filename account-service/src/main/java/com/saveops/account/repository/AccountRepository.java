package com.saveops.account.repository;

import com.saveops.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    List<AccountEntity> findByOwnerIdOrderByCreatedAtDesc(String ownerId);
}

