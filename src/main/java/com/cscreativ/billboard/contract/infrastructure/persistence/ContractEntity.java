package com.cscreativ.billboard.contract.infrastructure.persistence;

import com.cscreativ.billboard.contract.domain.ContractStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contracts")
public class ContractEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID bookingId;

    @Column(nullable = false)
    private UUID ownerId;

    @Column(nullable = false)
    private UUID advertiserId;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String termsAndConditions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;

    private String ownerSignedBy;
    private String ownerSignedIp;
    private LocalDateTime ownerSignedAt;

    private String advertiserSignedBy;
    private String advertiserSignedIp;
    private LocalDateTime advertiserSignedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBookingId() { return bookingId; }
    public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }
    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
    public UUID getAdvertiserId() { return advertiserId; }
    public void setAdvertiserId(UUID advertiserId) { this.advertiserId = advertiserId; }
    public String getTermsAndConditions() { return termsAndConditions; }
    public void setTermsAndConditions(String termsAndConditions) { this.termsAndConditions = termsAndConditions; }
    public ContractStatus getStatus() { return status; }
    public void setStatus(ContractStatus status) { this.status = status; }
    public String getOwnerSignedBy() { return ownerSignedBy; }
    public void setOwnerSignedBy(String ownerSignedBy) { this.ownerSignedBy = ownerSignedBy; }
    public String getOwnerSignedIp() { return ownerSignedIp; }
    public void setOwnerSignedIp(String ownerSignedIp) { this.ownerSignedIp = ownerSignedIp; }
    public LocalDateTime getOwnerSignedAt() { return ownerSignedAt; }
    public void setOwnerSignedAt(LocalDateTime ownerSignedAt) { this.ownerSignedAt = ownerSignedAt; }
    public String getAdvertiserSignedBy() { return advertiserSignedBy; }
    public void setAdvertiserSignedBy(String advertiserSignedBy) { this.advertiserSignedBy = advertiserSignedBy; }
    public String getAdvertiserSignedIp() { return advertiserSignedIp; }
    public void setAdvertiserSignedIp(String advertiserSignedIp) { this.advertiserSignedIp = advertiserSignedIp; }
    public LocalDateTime getAdvertiserSignedAt() { return advertiserSignedAt; }
    public void setAdvertiserSignedAt(LocalDateTime advertiserSignedAt) { this.advertiserSignedAt = advertiserSignedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
