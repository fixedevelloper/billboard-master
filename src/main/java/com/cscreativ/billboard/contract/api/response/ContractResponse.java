package com.cscreativ.billboard.contract.api.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContractResponse(
        UUID id,
        UUID bookingId,
        UUID ownerId,
        UUID advertiserId,
        String termsAndConditions,
        String status,
        boolean isSignedByOwner,
        boolean isSignedByAdvertiser,
        LocalDateTime createdAt
) {}
