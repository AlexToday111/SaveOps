package com.saveops.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntryEntity {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LedgerEntryType type;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;
    @Column(nullable = false)
    private String operationId;
    @Column(nullable = false)
    private String correlationId;
    @Column(nullable = false)
    private Instant createdAt;

    protected LedgerEntryEntity() {
    }

    public LedgerEntryEntity(UUID id, AccountEntity account, LedgerEntryType type, BigDecimal amount,
                             BigDecimal balanceAfter, String operationId, String correlationId, Instant createdAt) {
        this.id = id;
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.operationId = operationId;
        this.correlationId = correlationId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }
}

