package com.cscreativ.billboard.contract.domain;

import com.cscreativ.billboard.contract.domain.valueobject.SignatureDetails;

import java.time.LocalDateTime;
import java.util.UUID;

public class Contract {
    private UUID id;
    private UUID bookingId;
    private UUID ownerId;
    private UUID advertiserId;
    private String termsAndConditions;
    private ContractStatus status;
    private SignatureDetails ownerSignature;
    private SignatureDetails advertiserSignature;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Contract(UUID id, UUID bookingId, UUID ownerId, UUID advertiserId, String termsAndConditions, ContractStatus status, SignatureDetails ownerSignature, SignatureDetails advertiserSignature, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.ownerId = ownerId;
        this.advertiserId = advertiserId;
        this.termsAndConditions = termsAndConditions;
        this.status = status;
        this.ownerSignature = ownerSignature;
        this.advertiserSignature = advertiserSignature;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Contract create(UUID bookingId, UUID ownerId, UUID advertiserId, String termsAndConditions) {
        LocalDateTime now = LocalDateTime.now();
        return new Contract(UUID.randomUUID(), bookingId, ownerId, advertiserId, termsAndConditions, ContractStatus.DRAFT, null, null, now, now);
    }

    public void publishForSignature() {
        if (this.status != ContractStatus.DRAFT) {
            throw new IllegalStateException("Seul un contrat au statut BROUILLON peut être publié pour signature");
        }
        this.status = ContractStatus.PENDING_SIGNATURE;
        this.updatedAt = LocalDateTime.now();
    }

    public void signByOwner(String signerName, String ipAddress) {
        if (this.status != ContractStatus.PENDING_SIGNATURE) {
            throw new IllegalStateException("Le contrat n'est pas en attente de signature");
        }
        this.ownerSignature = new SignatureDetails(signerName, ipAddress, LocalDateTime.now());
        checkAndCompleteSignature();
    }

    public void signByAdvertiser(String signerName, String ipAddress) {
        if (this.status != ContractStatus.PENDING_SIGNATURE) {
            throw new IllegalStateException("Le contrat n'est pas en attente de signature");
        }
        this.advertiserSignature = new SignatureDetails(signerName, ipAddress, LocalDateTime.now());
        checkAndCompleteSignature();
    }

    private void checkAndCompleteSignature() {
        if (this.ownerSignature != null && this.advertiserSignature != null) {
            this.status = ContractStatus.SIGNED;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void terminate() {
        if (this.status != ContractStatus.SIGNED) {
            throw new IllegalStateException("Seul un contrat signé peut être résilié");
        }
        this.status = ContractStatus.TERMINATED;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getBookingId() { return bookingId; }
    public UUID getOwnerId() { return ownerId; }
    public UUID getAdvertiserId() { return advertiserId; }
    public String getTermsAndConditions() { return termsAndConditions; }
    public ContractStatus getStatus() { return status; }
    public SignatureDetails getOwnerSignature() { return ownerSignature; }
    public SignatureDetails getAdvertiserSignature() { return advertiserSignature; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
