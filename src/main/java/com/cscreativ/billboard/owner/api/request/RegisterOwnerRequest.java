package com.cscreativ.billboard.owner.api.request;

import java.math.BigDecimal;
import java.util.UUID;

public record RegisterOwnerRequest(
        UUID userId,
        String companyName,
        String registrationNumber,
        String contactEmail,
        String phoneNumber,
        BigDecimal revenueShareRate
) {}
