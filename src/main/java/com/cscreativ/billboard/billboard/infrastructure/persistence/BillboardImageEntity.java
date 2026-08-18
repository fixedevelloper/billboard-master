package com.cscreativ.billboard.billboard.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "billboard_images")
public class BillboardImageEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID billboardId;

    @Column(nullable = false)
    private UUID fileId;

    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBillboardId() { return billboardId; }
    public void setBillboardId(UUID billboardId) { this.billboardId = billboardId; }
    public UUID getFileId() { return fileId; }
    public void setFileId(UUID fileId) { this.fileId = fileId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
