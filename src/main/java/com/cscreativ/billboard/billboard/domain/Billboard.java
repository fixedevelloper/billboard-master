package com.cscreativ.billboard.billboard.domain;

import com.cscreativ.billboard.billboard.domain.valueobject.Dimensions;
import com.cscreativ.billboard.billboard.domain.valueobject.Location;
import com.cscreativ.billboard.billboard.domain.valueobject.Pricing;

import java.time.LocalDateTime;
import java.util.UUID;

public class Billboard {
    private UUID id;
    private String title;
    private String description;
    private BillboardType type;
    private BillboardStatus status;
    private Location location;
    private Dimensions dimensions;
    private Pricing pricing;
    private UUID ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Billboard(UUID id, String title, String description, BillboardType type, BillboardStatus status, Location location, Dimensions dimensions, Pricing pricing, UUID ownerId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.status = status;
        this.location = location;
        this.dimensions = dimensions;
        this.pricing = pricing;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Billboard create(String title, String description, BillboardType type, Location location, Dimensions dimensions, Pricing pricing, UUID ownerId) {
        LocalDateTime now = LocalDateTime.now();
        return new Billboard(UUID.randomUUID(), title, description, type, BillboardStatus.AVAILABLE, location, dimensions, pricing, ownerId, now, now);
    }

    public void markAsBooked() {
        this.status = BillboardStatus.BOOKED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsAvailable() {
        this.status = BillboardStatus.AVAILABLE;
        this.updatedAt = LocalDateTime.now();
    }

    public void putUnderMaintenance() {
        this.status = BillboardStatus.UNDER_MAINTENANCE;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDetails(String title, String description, BillboardType type,
                               Location location, Dimensions dimensions, Pricing pricing) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.location = location;
        this.dimensions = dimensions;
        this.pricing = pricing;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public BillboardType getType() { return type; }
    public BillboardStatus getStatus() { return status; }
    public Location getLocation() { return location; }
    public Dimensions getDimensions() { return dimensions; }
    public Pricing getPricing() { return pricing; }
    public UUID getOwnerId() { return ownerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
