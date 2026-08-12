package com.cscreativ.billboard.campaign.domain;

import com.cscreativ.billboard.campaign.domain.valueobject.MediaAsset;

import java.time.LocalDateTime;
import java.util.UUID;

public class Campaign {
    private UUID id;
    private UUID bookingId;
    private UUID advertiserId;
    private String name;
    private String description;
    private MediaAsset mediaAsset;
    private CampaignStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Campaign(UUID id, UUID bookingId, UUID advertiserId, String name, String description, MediaAsset mediaAsset, CampaignStatus status, String rejectionReason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.advertiserId = advertiserId;
        this.name = name;
        this.description = description;
        this.mediaAsset = mediaAsset;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Campaign create(UUID bookingId, UUID advertiserId, String name, String description, MediaAsset mediaAsset) {
        LocalDateTime now = LocalDateTime.now();
        return new Campaign(UUID.randomUUID(), bookingId, advertiserId, name, description, mediaAsset, CampaignStatus.DRAFT, null, now, now);
    }

    public void submitForApproval() {
        if (this.status != CampaignStatus.DRAFT && this.status != CampaignStatus.REJECTED) {
            throw new IllegalStateException("Seule une campagne en brouillon ou rejetée peut être soumise pour approbation");
        }
        this.status = CampaignStatus.SUBMITTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void approve() {
        if (this.status != CampaignStatus.SUBMITTED) {
            throw new IllegalStateException("Seule une campagne soumise peut être approuvée");
        }
        this.status = CampaignStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(String reason) {
        if (this.status != CampaignStatus.SUBMITTED) {
            throw new IllegalStateException("Seule une campagne soumise peut être rejetée");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Le motif de rejet est obligatoire");
        }
        this.status = CampaignStatus.REJECTED;
        this.rejectionReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    /** Déclenché par la pose physique du visuel (voir InstallationEventListener). */
    public void activate() {
        if (this.status != CampaignStatus.APPROVED) {
            throw new IllegalStateException("Seule une campagne approuvée peut être activée");
        }
        this.status = CampaignStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /** Déclenché par le passage de la date de fin de la réservation (voir CampaignCompletionScheduler). */
    public void complete() {
        if (this.status != CampaignStatus.ACTIVE) {
            throw new IllegalStateException("Seule une campagne active peut être terminée");
        }
        this.status = CampaignStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getBookingId() { return bookingId; }
    public UUID getAdvertiserId() { return advertiserId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public MediaAsset getMediaAsset() { return mediaAsset; }
    public CampaignStatus getStatus() { return status; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
