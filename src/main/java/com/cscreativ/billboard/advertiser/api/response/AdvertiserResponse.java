package com.cscreativ.billboard.advertiser.api.response;

import java.util.UUID;

public record AdvertiserResponse(
        UUID id,
        UUID userId,
        String companyName,
        String taxNumber,
        String contactEmail,
        String contactPhone,
        String status
) {}
