package com.saveops.common.logging;

import java.util.Optional;
import java.util.UUID;

public final class CorrelationId {
    public static final String HEADER = "X-Correlation-Id";
    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    private CorrelationId() {
    }

    public static String currentOrNew() {
        return Optional.ofNullable(CURRENT.get()).filter(value -> !value.isBlank()).orElseGet(CorrelationId::newId);
    }

    public static void set(String correlationId) {
        CURRENT.set(correlationId == null || correlationId.isBlank() ? newId() : correlationId);
    }

    public static void clear() {
        CURRENT.remove();
    }

    public static String newId() {
        return UUID.randomUUID().toString();
    }
}

