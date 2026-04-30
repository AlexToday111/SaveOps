package com.saveops.audit.repository;

import com.saveops.audit.entity.AuditEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, String> {
    List<AuditEventEntity> findByAggregateIdOrderByOccurredAtAsc(String aggregateId);
}

