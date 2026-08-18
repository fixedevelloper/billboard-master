package com.cscreativ.billboard.billboard.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class BillboardImage {
    private UUID id;
    private UUID billboardId;
    private UUID fileId;
    private LocalDateTime createdAt;

    public BillboardImage(UUID id, UUID billboardId, UUID fileId, LocalDateTime createdAt) {
        this.id = id;
        this.billboardId = billboardId;
        this.fileId = fileId;
        this.createdAt = createdAt;
    }

    public static BillboardImage create(UUID billboardId, UUID fileId) {
        return new BillboardImage(UUID.randomUUID(), billboardId, fileId, LocalDateTime.now());
    }

    public UUID getId() { return id; }
    public UUID getBillboardId() { return billboardId; }
    public UUID getFileId() { return fileId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
