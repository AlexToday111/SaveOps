package com.saveops.audit.dto;

import java.time.Instant;

public record AuditEventResponse(
        String eventId,
        String eventType,
        String aggregateId,
        Instant occurredAt,
        String correlationId,
        String payload,
        Instant savedAt
) {
}

