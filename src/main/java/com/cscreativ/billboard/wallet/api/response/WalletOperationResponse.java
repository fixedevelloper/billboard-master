package com.cscreativ.billboard.wallet.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WalletOperationResponse(
        UUID id,
        UUID userId,
        String type,
        String method,
        String status,
        BigDecimal amount,
        String currency,
        String phoneNumber,
        String bankAccountHolder,
        String bankIban,
        String bankName,
        String reference,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
