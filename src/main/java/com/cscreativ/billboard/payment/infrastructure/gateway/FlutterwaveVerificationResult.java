package com.cscreativ.billboard.payment.infrastructure.gateway;

import java.math.BigDecimal;

public record FlutterwaveVerificationResult(
        boolean successful,
        String txRef,
        String flutterwaveTransactionId,
        BigDecimal amount,
        String currency
) {}
