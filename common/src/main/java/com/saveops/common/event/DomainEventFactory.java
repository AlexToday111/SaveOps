package com.saveops.common.event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public final class DomainEventFactory {
    private DomainEventFactory() {
    }

    public static DomainEvent create(String eventType, String aggregateId, String correlationId, Map<String, Object> payload) {
        return new DomainEvent(UUID.randomUUID().toString(), eventType, aggregateId, Instant.now(), correlationId, payload);
    }
}

