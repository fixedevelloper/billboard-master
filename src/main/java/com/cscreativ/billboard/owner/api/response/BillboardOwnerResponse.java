package com.cscreativ.billboard.owner.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BillboardOwnerResponse(
        UUID id,
        UUID userId,
        String companyName,
        String registrationNumber,
        String contactEmail,
        String phoneNumber,
        BigDecimal revenueShareRate,
        String status,
        LocalDateTime createdAt
) {}
