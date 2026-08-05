package com.cscreativ.billboard.payment.domain;

import com.cscreativ.billboard.payment.domain.valueobject.Money;

import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentTransaction {
    private UUID id;
    private UUID payerId;
    private UUID referenceId; // Ex: ID de campagne ou de réservation
    private Money money;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String gatewayReference;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PaymentTransaction(UUID id, UUID payerId, UUID referenceId, Money money, PaymentMethod paymentMethod, PaymentStatus status, String gatewayReference, String failureReason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.payerId = payerId;
        this.referenceId = referenceId;
        this.money = money;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.gatewayReference = gatewayReference;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PaymentTransaction initiate(UUID payerId, UUID referenceId, Money money, PaymentMethod paymentMethod) {
        LocalDateTime now = LocalDateTime.now();
        return new PaymentTransaction(UUID.randomUUID(), payerId, referenceId, money, paymentMethod, PaymentStatus.PENDING, null, null, now, now);
    }

    public void markAsSuccessful(String gatewayReference) {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Seul un paiement en attente peut être marqué comme réussi");
        }
        this.status = PaymentStatus.SUCCESSFUL;
        this.gatewayReference = gatewayReference;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFailed(String failureReason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
        this.updatedAt = LocalDateTime.now();
    }

    public void refund() {
        if (this.status != PaymentStatus.SUCCESSFUL) {
            throw new IllegalStateException("Seul un paiement réussi peut être remboursé");
        }
        this.status = PaymentStatus.REFUNDED;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getPayerId() { return payerId; }
    public UUID getReferenceId() { return referenceId; }
    public Money getMoney() { return money; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getStatus() { return status; }
    public String getGatewayReference() { return gatewayReference; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
