package com.cscreativ.billboard.owner.domain;

import com.cscreativ.billboard.owner.domain.valueobject.OwnerDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BillboardOwner {
    private UUID id;
    private UUID userId;
    private OwnerDetails ownerDetails;
    private BigDecimal revenueShareRate;
    private OwnerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BillboardOwner(UUID id, UUID userId, OwnerDetails ownerDetails, BigDecimal revenueShareRate, OwnerStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.ownerDetails = ownerDetails;
        this.revenueShareRate = revenueShareRate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static BillboardOwner create(UUID userId, OwnerDetails ownerDetails, BigDecimal revenueShareRate) {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal defaultRate = revenueShareRate != null ? revenueShareRate : BigDecimal.ZERO;
        return new BillboardOwner(UUID.randomUUID(), userId, ownerDetails, defaultRate, OwnerStatus.PENDING_APPROVAL, now, now);
    }

    public void activate() {
        if (this.status == OwnerStatus.ACTIVE) {
            throw new IllegalStateException("Le régisseur est déjà actif");
        }
        this.status = OwnerStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        this.status = OwnerStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateRevenueShareRate(BigDecimal newRate) {
        if (newRate == null || newRate.compareTo(BigDecimal.ZERO) < 0 || newRate.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Le taux de commission doit être compris entre 0 et 100%");
        }
        this.revenueShareRate = newRate;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public OwnerDetails getOwnerDetails() { return ownerDetails; }
    public BigDecimal getRevenueShareRate() { return revenueShareRate; }
    public OwnerStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
