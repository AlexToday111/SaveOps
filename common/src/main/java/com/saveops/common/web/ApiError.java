package com.saveops.common.web;

import java.time.Instant;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String correlationId,
        String path
) {
}

