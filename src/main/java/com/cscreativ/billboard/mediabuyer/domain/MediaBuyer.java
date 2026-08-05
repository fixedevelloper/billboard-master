package com.cscreativ.billboard.mediabuyer.domain;

import com.cscreativ.billboard.mediabuyer.domain.valueobject.CompanyDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class MediaBuyer {
    private UUID id;
    private UUID userId;
    private CompanyDetails companyDetails;
    private BigDecimal creditLimit;
    private BigDecimal currentSpent;
    private BuyerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MediaBuyer(UUID id, UUID userId, CompanyDetails companyDetails, BigDecimal creditLimit, BigDecimal currentSpent, BuyerStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.companyDetails = companyDetails;
        this.creditLimit = creditLimit;
        this.currentSpent = currentSpent;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MediaBuyer create(UUID userId, CompanyDetails companyDetails, BigDecimal creditLimit) {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal initialCredit = creditLimit != null ? creditLimit : BigDecimal.ZERO;
        return new MediaBuyer(UUID.randomUUID(), userId, companyDetails, initialCredit, BigDecimal.ZERO, BuyerStatus.PENDING_VERIFICATION, now, now);
    }

    public void activate() {
        if (this.status == BuyerStatus.ACTIVE) {
            throw new IllegalStateException("L'acheteur est déjà actif");
        }
        this.status = BuyerStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        this.status = BuyerStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateCreditLimit(BigDecimal newLimit) {
        if (newLimit == null || newLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le plafond de crédit doit être positif");
        }
        this.creditLimit = newLimit;
        this.updatedAt = LocalDateTime.now();
    }

    public void recordSpending(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant des dépenses doit être supérieur à zéro");
        }
        this.currentSpent = this.currentSpent.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canSpend(BigDecimal amount) {
        if (this.status != BuyerStatus.ACTIVE) return false;
        BigDecimal projectedSpent = this.currentSpent.add(amount);
        return projectedSpent.compareTo(this.creditLimit) <= 0;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public CompanyDetails getCompanyDetails() { return companyDetails; }
    public BigDecimal getCreditLimit() { return creditLimit; }
    public BigDecimal getCurrentSpent() { return currentSpent; }
    public BuyerStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
