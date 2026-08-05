package com.cscreativ.billboard.payment.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentTransactionResponse(
        UUID id,
        UUID payerId,
        UUID referenceId,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        String status,
        String gatewayReference,
        String failureReason,
        LocalDateTime createdAt
) {}
