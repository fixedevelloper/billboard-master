package com.cscreativ.billboard.advertiser.domain;

import com.cscreativ.billboard.advertiser.domain.valueobject.CompanyName;
import com.cscreativ.billboard.advertiser.domain.valueobject.TaxNumber;

import java.time.LocalDateTime;
import java.util.UUID;

public class Advertiser {
    private UUID id;
    private UUID userId;
    private CompanyName companyName;
    private TaxNumber taxNumber;
    private String contactEmail;
    private String contactPhone;
    private AdvertiserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Advertiser(UUID id, UUID userId, CompanyName companyName, TaxNumber taxNumber, String contactEmail, String contactPhone, AdvertiserStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.companyName = companyName;
        this.taxNumber = taxNumber;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Advertiser create(UUID userId, CompanyName companyName, TaxNumber taxNumber, String contactEmail, String contactPhone) {
        LocalDateTime now = LocalDateTime.now();
        return new Advertiser(UUID.randomUUID(), userId, companyName, taxNumber, contactEmail, contactPhone, AdvertiserStatus.PENDING_VERIFICATION, now, now);
    }

    public void verify() {
        this.status = AdvertiserStatus.VERIFIED;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        this.status = AdvertiserStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public CompanyName getCompanyName() { return companyName; }
    public TaxNumber getTaxNumber() { return taxNumber; }
    public String getContactEmail() { return contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public AdvertiserStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
