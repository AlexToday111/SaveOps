package com.saveops.goal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "savings_goals")
public class SavingsGoalEntity {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String ownerId;
    @Column(nullable = false)
    private UUID accountId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal targetAmount;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    private Instant createdAt;

    protected SavingsGoalEntity() {
    }

    public SavingsGoalEntity(UUID id, String ownerId, UUID accountId, String name, BigDecimal targetAmount, String currency, Instant createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.accountId = accountId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public String getCurrency() {
        return currency;
    }
}

