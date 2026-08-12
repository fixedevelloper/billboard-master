package com.cscreativ.billboard.wallet.infrastructure.persistence;

import com.cscreativ.billboard.wallet.domain.WalletOperationMethod;
import com.cscreativ.billboard.wallet.domain.WalletOperationStatus;
import com.cscreativ.billboard.wallet.domain.WalletTransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallet_operations")
public class WalletOperationEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletTransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletOperationMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletOperationStatus status;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    private String phoneNumber;
    private String bankAccountHolder;
    private String bankIban;
    private String bankName;
    private String reference;
    private String failureReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public WalletTransactionType getType() { return type; }
    public void setType(WalletTransactionType type) { this.type = type; }
    public WalletOperationMethod getMethod() { return method; }
    public void setMethod(WalletOperationMethod method) { this.method = method; }
    public WalletOperationStatus getStatus() { return status; }
    public void setStatus(WalletOperationStatus status) { this.status = status; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getBankAccountHolder() { return bankAccountHolder; }
    public void setBankAccountHolder(String bankAccountHolder) { this.bankAccountHolder = bankAccountHolder; }
    public String getBankIban() { return bankIban; }
    public void setBankIban(String bankIban) { this.bankIban = bankIban; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
