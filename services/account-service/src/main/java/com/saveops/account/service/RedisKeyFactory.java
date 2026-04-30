package com.saveops.account.service;

import java.util.UUID;

public final class RedisKeyFactory {
    private RedisKeyFactory() {
    }

    public static String idempotencyKey(String operationId) {
        return "account:idempotency:" + operationId;
    }

    public static String balanceKey(UUID accountId) {
        return "account:balance:" + accountId;
    }
}

