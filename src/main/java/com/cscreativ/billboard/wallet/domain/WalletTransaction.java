package com.cscreativ.billboard.wallet.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class WalletTransaction {
    private UUID id;
    private UUID walletId;
    private WalletTransactionType type;
    private BigDecimal amount;
    private String currency;
    private String reference;
    private LocalDateTime createdAt;

    public WalletTransaction(UUID id, UUID walletId, WalletTransactionType type, BigDecimal amount, String currency, String reference, LocalDateTime createdAt) {
        this.id = id;
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.reference = reference;
        this.createdAt = createdAt;
    }

    public static WalletTransaction create(UUID walletId, WalletTransactionType type, BigDecimal amount, String currency, String reference) {
        return new WalletTransaction(UUID.randomUUID(), walletId, type, amount, currency, reference, LocalDateTime.now());
    }

    public UUID getId() { return id; }
    public UUID getWalletId() { return walletId; }
    public WalletTransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getReference() { return reference; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
