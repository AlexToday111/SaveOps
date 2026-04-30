package com.saveops.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "audit_events")
public class AuditEventEntity {
    @Id
    private String eventId;
    @Column(nullable = false)
    private String eventType;
    @Column(nullable = false)
    private String aggregateId;
    @Column(nullable = false)
    private Instant occurredAt;
    @Column(nullable = false)
    private String correlationId;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;
    @Column(nullable = false)
    private Instant savedAt;

    protected AuditEventEntity() {
    }

    public AuditEventEntity(String eventId, String eventType, String aggregateId, Instant occurredAt,
                            String correlationId, String payload, Instant savedAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.occurredAt = occurredAt;
        this.correlationId = correlationId;
        this.payload = payload;
        this.savedAt = savedAt;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getSavedAt() {
        return savedAt;
    }
}

