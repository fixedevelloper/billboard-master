package com.cscreativ.billboard.campaign.infrastructure.persistence;

import com.cscreativ.billboard.campaign.domain.CampaignStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "campaigns")
public class CampaignEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID bookingId;

    @Column(nullable = false)
    private UUID advertiserId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(length = 2000)
    private String mediaUrl;
    private String mediaFileType;
    private long mediaFileSizeInBytes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignStatus status;

    private String rejectionReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBookingId() { return bookingId; }
    public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }
    public UUID getAdvertiserId() { return advertiserId; }
    public void setAdvertiserId(UUID advertiserId) { this.advertiserId = advertiserId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public String getMediaFileType() { return mediaFileType; }
    public void setMediaFileType(String mediaFileType) { this.mediaFileType = mediaFileType; }
    public long getMediaFileSizeInBytes() { return mediaFileSizeInBytes; }
    public void setMediaFileSizeInBytes(long mediaFileSizeInBytes) { this.mediaFileSizeInBytes = mediaFileSizeInBytes; }
    public CampaignStatus getStatus() { return status; }
    public void setStatus(CampaignStatus status) { this.status = status; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
