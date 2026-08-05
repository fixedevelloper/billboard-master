package com.cscreativ.billboard.creative.api.request;

import java.util.UUID;

public record SubmitProofRequest(
        UUID campaignId,
        String fileUrl,
        int width,
        int height
) {}
