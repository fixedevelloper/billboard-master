package com.cscreativ.billboard.advertiser.api.request;

import java.util.UUID;

public record RegisterAdvertiserRequest(
        UUID userId,
        String companyName,
        String taxNumber,
        String contactEmail,
        String contactPhone
) {}
