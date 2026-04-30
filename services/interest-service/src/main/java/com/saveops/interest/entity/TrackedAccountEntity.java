package com.saveops.interest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tracked_accounts")
public class TrackedAccountEntity {
    @Id
    private UUID accountId;
    @Column(nullable = false)
    private String ownerId;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    private Instant trackedAt;

    protected TrackedAccountEntity() {
    }

    public TrackedAccountEntity(UUID accountId, String ownerId, String currency, Instant trackedAt) {
        this.accountId = accountId;
        this.ownerId = ownerId;
        this.currency = currency;
        this.trackedAt = trackedAt;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getCurrency() {
        return currency;
    }
}

