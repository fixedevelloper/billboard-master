package com.cscreativ.billboard.billboard.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class BillboardImage {
    private UUID id;
    private UUID billboardId;
    private String url;
    private LocalDateTime createdAt;

    public BillboardImage(UUID id, UUID billboardId, String url, LocalDateTime createdAt) {
        this.id = id;
        this.billboardId = billboardId;
        this.url = url;
        this.createdAt = createdAt;
    }

    public static BillboardImage create(UUID billboardId, String url) {
        return new BillboardImage(UUID.randomUUID(), billboardId, url, LocalDateTime.now());
    }

    public UUID getId() { return id; }
    public UUID getBillboardId() { return billboardId; }
    public String getUrl() { return url; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
