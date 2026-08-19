package com.cscreativ.billboard.owner.domain;

import com.cscreativ.billboard.owner.domain.valueobject.OwnerDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BillboardOwner {

    /**
     * Taux de reversement à la plateforme appliqué par défaut à tout nouveau propriétaire :
     * défini par la plateforme, pas par le propriétaire (voir BecomeOwnerPage côté frontend, qui
     * n'expose plus ce champ en saisie). Un admin peut l'ajuster ensuite via updateRevenueShareRate.
     */
    public static final BigDecimal DEFAULT_REVENUE_SHARE_RATE = new BigDecimal("0.10");

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

    public static BillboardOwner create(UUID userId, OwnerDetails ownerDetails) {
        LocalDateTime now = LocalDateTime.now();
        return new BillboardOwner(UUID.randomUUID(), userId, ownerDetails, DEFAULT_REVENUE_SHARE_RATE, OwnerStatus.PENDING_APPROVAL, now, now);
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
        validateRate(newRate);
        this.revenueShareRate = newRate;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * revenueShareRate est une fraction (0 à 1) de la commission plateforme, pas un pourcentage
     * sur 100 (0.15 = 15%) — voir le libellé frontend "Taux de reversement à la plateforme (0 à
     * 1)". La répartition d'un paiement (booking.RevenueSplitListener) suppose cette borne pour
     * calculer la part du propriétaire.
     */
    private static void validateRate(BigDecimal rate) {
        if (rate == null || rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Le taux de commission doit être compris entre 0 et 1 (ex. 0.15 pour 15%)");
        }
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public OwnerDetails getOwnerDetails() { return ownerDetails; }
    public BigDecimal getRevenueShareRate() { return revenueShareRate; }
    public OwnerStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
