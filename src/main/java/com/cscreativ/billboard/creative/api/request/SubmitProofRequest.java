package com.cscreativ.billboard.creative.api.request;

import java.util.UUID;

public record SubmitProofRequest(
        UUID campaignId,
        UUID fileId,
        int width,
        int height
) {}
