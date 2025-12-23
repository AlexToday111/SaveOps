package com.saveops.account.service;

import java.math.BigDecimal;
import java.util.UUID;

public record MoneyOperationResult(UUID accountId, String currency, BigDecimal balance, UUID ledgerEntryId) {
}

