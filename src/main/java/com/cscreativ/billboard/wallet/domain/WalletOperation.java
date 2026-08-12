package com.cscreativ.billboard.wallet.domain;

import com.cscreativ.billboard.wallet.domain.valueobject.BankDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Cycle de vie d'une demande de dépôt ou de retrait (Mobile Money / virement bancaire),
 * distinct du grand livre WalletTransaction : une WalletOperation existe dès PENDING, avant
 * tout mouvement de solde. Le retrait débite le portefeuille dès l'initiation (voir
 * WalletOperationService.initiateWithdrawal) ; le dépôt ne crédite qu'à complete().
 */
public class WalletOperation {
    private UUID id;
    private UUID userId;
    private WalletTransactionType type;
    private WalletOperationMethod method;
    private WalletOperationStatus status;
    private BigDecimal amount;
    private String currency;
    private String phoneNumber;
    private BankDetails bankDetails;
    private String reference;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WalletOperation(UUID id, UUID userId, WalletTransactionType type, WalletOperationMethod method,
                            WalletOperationStatus status, BigDecimal amount, String currency, String phoneNumber,
                            BankDetails bankDetails, String reference, String failureReason,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.method = method;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.phoneNumber = phoneNumber;
        this.bankDetails = bankDetails;
        this.reference = reference;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static WalletOperation initiateDeposit(UUID userId, WalletOperationMethod method, BigDecimal amount,
                                                    String currency, String phoneNumber) {
        validateAmount(amount);
        UUID id = UUID.randomUUID();
        String phone = requirePhoneForMobileMoney(method, phoneNumber);
        // Référence à mentionner par l'utilisateur sur son virement pour rapprochement manuel.
        String reference = method == WalletOperationMethod.BANK_TRANSFER
                ? "DEP-" + id.toString().substring(0, 8).toUpperCase()
                : null;
        LocalDateTime now = LocalDateTime.now();
        return new WalletOperation(id, userId, WalletTransactionType.DEPOSIT, method,
                WalletOperationStatus.PENDING, amount, currency, phone, null, reference, null, now, now);
    }

    public static WalletOperation initiateWithdrawal(UUID userId, WalletOperationMethod method, BigDecimal amount,
                                                       String currency, String phoneNumber, BankDetails bankDetails) {
        validateAmount(amount);
        String phone = requirePhoneForMobileMoney(method, phoneNumber);
        if (method == WalletOperationMethod.BANK_TRANSFER && bankDetails == null) {
            throw new IllegalArgumentException("Les coordonnées bancaires du destinataire sont obligatoires pour un retrait par virement");
        }
        LocalDateTime now = LocalDateTime.now();
        return new WalletOperation(UUID.randomUUID(), userId, WalletTransactionType.WITHDRAWAL, method,
                WalletOperationStatus.PENDING, amount, currency, phone,
                method == WalletOperationMethod.BANK_TRANSFER ? bankDetails : null,
                null, null, now, now);
    }

    private static String requirePhoneForMobileMoney(WalletOperationMethod method, String phoneNumber) {
        if (method == WalletOperationMethod.MOBILE_MONEY) {
            if (phoneNumber == null || phoneNumber.isBlank()) {
                throw new IllegalArgumentException("Le numéro de téléphone Mobile Money est obligatoire");
            }
            return phoneNumber;
        }
        return null;
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro");
        }
    }

    public void complete() {
        if (this.status != WalletOperationStatus.PENDING) {
            throw new IllegalStateException("Seule une opération en attente peut être complétée");
        }
        this.status = WalletOperationStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void fail(String reason) {
        if (this.status != WalletOperationStatus.PENDING) {
            throw new IllegalStateException("Seule une opération en attente peut échouer");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Le motif d'échec est obligatoire");
        }
        this.status = WalletOperationStatus.FAILED;
        this.failureReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public WalletTransactionType getType() { return type; }
    public WalletOperationMethod getMethod() { return method; }
    public WalletOperationStatus getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getPhoneNumber() { return phoneNumber; }
    public BankDetails getBankDetails() { return bankDetails; }
    public String getReference() { return reference; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
