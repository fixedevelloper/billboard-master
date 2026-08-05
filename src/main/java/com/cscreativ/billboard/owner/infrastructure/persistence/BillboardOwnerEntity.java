package com.cscreativ.billboard.owner.infrastructure.persistence;

import com.cscreativ.billboard.owner.domain.OwnerStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "billboard_owners")
public class BillboardOwnerEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private String companyName;

    private String registrationNumber;

    @Column(nullable = false)
    private String contactEmail;

    private String phoneNumber;

    @Column(nullable = false)
    private BigDecimal revenueShareRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OwnerStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public BigDecimal getRevenueShareRate() { return revenueShareRate; }
    public void setRevenueShareRate(BigDecimal revenueShareRate) { this.revenueShareRate = revenueShareRate; }
    public OwnerStatus getStatus() { return status; }
    public void setStatus(OwnerStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
