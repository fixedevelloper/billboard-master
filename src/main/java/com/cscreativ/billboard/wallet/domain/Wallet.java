package com.cscreativ.billboard.wallet.domain;

import com.cscreativ.billboard.wallet.domain.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Wallet {
    private UUID id;
    private UUID userId;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Wallet(UUID id, UUID userId, BigDecimal balance, String currency, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.currency = currency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Wallet createEmpty(UUID userId, String currency) {
        LocalDateTime now = LocalDateTime.now();
        return new Wallet(UUID.randomUUID(), userId, BigDecimal.ZERO, currency, now, now);
    }

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant du dépôt doit être supérieur à zéro");
        }
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant du retrait doit être supérieur à zéro");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Solde insuffisant pour effectuer ce retrait");
        }
        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public BigDecimal getBalance() { return balance; }
    public String getCurrency() { return currency; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
