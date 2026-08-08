package com.cscreativ.billboard.payment.api.request;

import com.cscreativ.billboard.payment.domain.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

public record InitiatePaymentRequest(
        UUID payerId,
        UUID referenceId,
        BigDecimal amount,
        String currency,
        PaymentMethod paymentMethod,
        UUID initiatedBy
) {}
