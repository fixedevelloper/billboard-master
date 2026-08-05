package com.cscreativ.billboard.mediabuyer.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MediaBuyerResponse(
        UUID id,
        UUID userId,
        String companyName,
        String taxId,
        String contactEmail,
        String phoneNumber,
        BigDecimal creditLimit,
        BigDecimal currentSpent,
        String status,
        LocalDateTime createdAt
) {}
