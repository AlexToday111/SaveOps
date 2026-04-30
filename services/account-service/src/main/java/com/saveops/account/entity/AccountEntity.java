package com.saveops.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class AccountEntity {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String ownerId;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;
    @Column(nullable = false)
    private Instant createdAt;
    private Instant closedAt;
    @Version
    private long version;

    protected AccountEntity() {
    }

    public AccountEntity(UUID id, String ownerId, String currency, BigDecimal balance, AccountStatus status, Instant createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void deposit(BigDecimal amount) {
        ensureActive();
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        ensureActive();
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient account balance");
        }
        balance = balance.subtract(amount);
    }

    public void close() {
        ensureActive();
        status = AccountStatus.CLOSED;
        closedAt = Instant.now();
    }

    private void ensureActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
    }
}

