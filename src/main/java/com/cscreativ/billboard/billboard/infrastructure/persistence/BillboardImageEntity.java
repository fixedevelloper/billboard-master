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

    // URL présignée MinIO potentiellement longue (signature en query string).
    @Column(nullable = false, length = 2000)
    private String url;

    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBillboardId() { return billboardId; }
    public void setBillboardId(UUID billboardId) { this.billboardId = billboardId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
