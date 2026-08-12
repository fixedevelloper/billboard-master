package com.cscreativ.billboard.wallet.api.request;

import com.cscreativ.billboard.wallet.domain.WalletOperationMethod;

import java.math.BigDecimal;

/** phoneNumber requis si method = MOBILE_MONEY, ignoré pour BANK_TRANSFER (spécification 1). */
public record DepositRequest(
        WalletOperationMethod method,
        BigDecimal amount,
        String currency,
        String phoneNumber
) {}
