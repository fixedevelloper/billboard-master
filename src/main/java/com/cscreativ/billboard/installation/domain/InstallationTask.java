package com.cscreativ.billboard.installation.domain;

import com.cscreativ.billboard.installation.domain.valueobject.InstallationProof;

import java.time.LocalDateTime;
import java.util.UUID;

public class InstallationTask {
    private UUID id;
    private UUID campaignId;
    private UUID billboardId;
    private UUID technicianId;
    private LocalDateTime scheduledDate;
    private TaskStatus status;
    private InstallationProof proof;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InstallationTask(UUID id, UUID campaignId, UUID billboardId, UUID technicianId, LocalDateTime scheduledDate, TaskStatus status, InstallationProof proof, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.campaignId = campaignId;
        this.billboardId = billboardId;
        this.technicianId = technicianId;
        this.scheduledDate = scheduledDate;
        this.status = status;
        this.proof = proof;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static InstallationTask schedule(UUID campaignId, UUID billboardId, UUID technicianId, LocalDateTime scheduledDate) {
        LocalDateTime now = LocalDateTime.now();
        return new InstallationTask(UUID.randomUUID(), campaignId, billboardId, technicianId, scheduledDate, TaskStatus.SCHEDULED, null, now, now);
    }

    public void startInstallation() {
        if (this.status != TaskStatus.SCHEDULED) {
            throw new IllegalStateException("Seule une tâche planifiée peut être démarrée");
        }
        this.status = TaskStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }

    public void completeInstallation(String photoUrl, String notes) {
        if (this.status != TaskStatus.IN_PROGRESS && this.status != TaskStatus.SCHEDULED) {
            throw new IllegalStateException("La tâche ne peut pas être terminée dans son état actuel");
        }
        this.proof = new InstallationProof(photoUrl, notes, LocalDateTime.now());
        this.status = TaskStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFailed(String notes) {
        this.status = TaskStatus.FAILED;
        this.proof = new InstallationProof("N/A", notes, LocalDateTime.now());
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getCampaignId() { return campaignId; }
    public UUID getBillboardId() { return billboardId; }
    public UUID getTechnicianId() { return technicianId; }
    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public TaskStatus getStatus() { return status; }
    public InstallationProof getProof() { return proof; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
