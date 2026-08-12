package com.cscreativ.billboard.wallet.api.request;

import com.cscreativ.billboard.wallet.domain.WalletOperationMethod;

import java.math.BigDecimal;

/**
 * phoneNumber requis si method = MOBILE_MONEY ; bankAccountHolder/bankIban/bankName requis si
 * method = BANK_TRANSFER (coordonnées du DESTINATAIRE du virement, spécification 2).
 */
public record WithdrawalRequest(
        WalletOperationMethod method,
        BigDecimal amount,
        String currency,
        String phoneNumber,
        String bankAccountHolder,
        String bankIban,
        String bankName
) {}
