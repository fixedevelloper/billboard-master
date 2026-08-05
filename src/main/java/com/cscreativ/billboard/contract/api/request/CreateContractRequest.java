package com.cscreativ.billboard.contract.api.request;

import java.util.UUID;

public record CreateContractRequest(
        UUID bookingId,
        UUID ownerId,
        UUID advertiserId,
        String termsAndConditions
) {}
