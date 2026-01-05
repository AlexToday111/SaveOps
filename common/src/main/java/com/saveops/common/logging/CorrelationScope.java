package com.saveops.common.logging;

import org.slf4j.MDC;

public final class CorrelationScope implements AutoCloseable {
    private CorrelationScope(String correlationId) {
        CorrelationId.set(correlationId);
        MDC.put("correlationId", CorrelationId.currentOrNew());
    }

    public static CorrelationScope open(String correlationId) {
        return new CorrelationScope(correlationId);
    }

    @Override
    public void close() {
        MDC.remove("correlationId");
        CorrelationId.clear();
    }
}

