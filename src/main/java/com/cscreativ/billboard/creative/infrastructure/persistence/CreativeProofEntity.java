package com.cscreativ.billboard.creative.infrastructure.persistence;

import com.cscreativ.billboard.creative.domain.ProofStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "creative_proofs")
public class CreativeProofEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID campaignId;

    @Column(nullable = false)
    private int version;

    @Column(nullable = false)
    private String fileUrl;

    private int widthInPixels;
    private int heightInPixels;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProofStatus status;

    private String feedback;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCampaignId() { return campaignId; }
    public void setCampaignId(UUID campaignId) { this.campaignId = campaignId; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public int getWidthInPixels() { return widthInPixels; }
    public void setWidthInPixels(int widthInPixels) { this.widthInPixels = widthInPixels; }
    public int getHeightInPixels() { return heightInPixels; }
    public void setHeightInPixels(int heightInPixels) { this.heightInPixels = heightInPixels; }
    public ProofStatus getStatus() { return status; }
    public void setStatus(ProofStatus status) { this.status = status; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
