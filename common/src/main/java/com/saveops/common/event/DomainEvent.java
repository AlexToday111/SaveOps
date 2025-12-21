package com.saveops.common.event;

import java.time.Instant;
import java.util.Map;

public record DomainEvent(
        String eventId,
        String eventType,
        String aggregateId,
        Instant occurredAt,
        String correlationId,
        Map<String, Object> payload
) {
}

