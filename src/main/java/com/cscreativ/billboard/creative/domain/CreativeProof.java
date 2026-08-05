package com.cscreativ.billboard.creative.domain;

import com.cscreativ.billboard.creative.domain.valueobject.ProofDimensions;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreativeProof {
    private UUID id;
    private UUID campaignId;
    private int version;
    private String fileUrl;
    private ProofDimensions dimensions;
    private ProofStatus status;
    private String feedback;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CreativeProof(UUID id, UUID campaignId, int version, String fileUrl, ProofDimensions dimensions, ProofStatus status, String feedback, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.campaignId = campaignId;
        this.version = version;
        this.fileUrl = fileUrl;
        this.dimensions = dimensions;
        this.status = status;
        this.feedback = feedback;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CreativeProof create(UUID campaignId, int version, String fileUrl, ProofDimensions dimensions) {
        LocalDateTime now = LocalDateTime.now();
        return new CreativeProof(UUID.randomUUID(), campaignId, version, fileUrl, dimensions, ProofStatus.PENDING_REVIEW, null, now, now);
    }

    public void approve() {
        if (this.status != ProofStatus.PENDING_REVIEW) {
            throw new IllegalStateException("Seule une épreuve en attente de révision peut être approuvée");
        }
        this.status = ProofStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }

    public void requestRevision(String feedback) {
        if (this.status != ProofStatus.PENDING_REVIEW) {
            throw new IllegalStateException("Seule une épreuve en attente de révision peut faire l'objet d'une demande de modification");
        }
        if (feedback == null || feedback.isBlank()) {
            throw new IllegalArgumentException("Les commentaires de révision sont obligatoires");
        }
        this.status = ProofStatus.REVISION_REQUESTED;
        this.feedback = feedback;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(String reason) {
        if (this.status != ProofStatus.PENDING_REVIEW) {
            throw new IllegalStateException("Seule une épreuve en attente de révision peut être rejetée");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Le motif du rejet est obligatoire");
        }
        this.status = ProofStatus.REJECTED;
        this.feedback = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getCampaignId() { return campaignId; }
    public int getVersion() { return version; }
    public String getFileUrl() { return fileUrl; }
    public ProofDimensions getDimensions() { return dimensions; }
    public ProofStatus getStatus() { return status; }
    public String getFeedback() { return feedback; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
