package com.saveops.account.service;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResult(UUID accountId, String ownerId, String status, String currency, BigDecimal balance) {
}

