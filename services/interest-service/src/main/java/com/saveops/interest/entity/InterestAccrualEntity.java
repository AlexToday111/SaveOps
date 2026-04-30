package com.saveops.interest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "interest_accruals")
public class InterestAccrualEntity {
    @Id
    private UUID id;
    @Column(nullable = false)
    private UUID accountId;
    @Column(nullable = false)
    private LocalDate accrualDate;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    private Instant createdAt;

    protected InterestAccrualEntity() {
    }

    public InterestAccrualEntity(UUID id, UUID accountId, LocalDate accrualDate, BigDecimal amount, String currency, Instant createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.accrualDate = accrualDate;
        this.amount = amount;
        this.currency = currency;
        this.createdAt = createdAt;
    }
}

