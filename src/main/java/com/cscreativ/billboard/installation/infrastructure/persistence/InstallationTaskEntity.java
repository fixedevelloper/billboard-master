package com.cscreativ.billboard.installation.infrastructure.persistence;

import com.cscreativ.billboard.installation.domain.TaskStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "installation_tasks")
public class InstallationTaskEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID campaignId;

    @Column(nullable = false)
    private UUID billboardId;

    @Column(nullable = false)
    private UUID technicianId;

    @Column(nullable = false)
    private LocalDateTime scheduledDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(length = 2000)
    private String proofPhotoUrl;
    private String proofNotes;
    private LocalDateTime proofUploadedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCampaignId() { return campaignId; }
    public void setCampaignId(UUID campaignId) { this.campaignId = campaignId; }
    public UUID getBillboardId() { return billboardId; }
    public void setBillboardId(UUID billboardId) { this.billboardId = billboardId; }
    public UUID getTechnicianId() { return technicianId; }
    public void setTechnicianId(UUID technicianId) { this.technicianId = technicianId; }
    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public String getProofPhotoUrl() { return proofPhotoUrl; }
    public void setProofPhotoUrl(String proofPhotoUrl) { this.proofPhotoUrl = proofPhotoUrl; }
    public String getProofNotes() { return proofNotes; }
    public void setProofNotes(String proofNotes) { this.proofNotes = proofNotes; }
    public LocalDateTime getProofUploadedAt() { return proofUploadedAt; }
    public void setProofUploadedAt(LocalDateTime proofUploadedAt) { this.proofUploadedAt = proofUploadedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
