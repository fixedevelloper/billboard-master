package com.cscreativ.billboard.mediabuyer.api.request;

import java.math.BigDecimal;
import java.util.UUID;

public record RegisterBuyerRequest(
        UUID userId,
        String companyName,
        String taxId,
        String contactEmail,
        String phoneNumber,
        BigDecimal creditLimit
) {}
