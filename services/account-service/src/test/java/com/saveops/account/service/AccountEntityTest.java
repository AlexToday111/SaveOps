package com.saveops.account.service;

import com.saveops.account.entity.AccountEntity;
import com.saveops.account.entity.AccountStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountEntityTest {
    @Test
    void depositAndWithdrawUpdateBalance() {
        AccountEntity account = new AccountEntity(UUID.randomUUID(), "user-1", "RUB", BigDecimal.ZERO, AccountStatus.ACTIVE, Instant.now());

        account.deposit(new BigDecimal("1000.00"));
        account.withdraw(new BigDecimal("150.50"));

        assertThat(account.getBalance()).isEqualByComparingTo("849.50");
    }

    @Test
    void withdrawRejectsInsufficientBalance() {
        AccountEntity account = new AccountEntity(UUID.randomUUID(), "user-1", "RUB", new BigDecimal("100.00"), AccountStatus.ACTIVE, Instant.now());

        assertThatThrownBy(() -> account.withdraw(new BigDecimal("150.00")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient");
    }
}

