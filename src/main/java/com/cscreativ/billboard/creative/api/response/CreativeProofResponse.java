package com.cscreativ.billboard.creative.api.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreativeProofResponse(
        UUID id,
        UUID campaignId,
        int version,
        String fileUrl,
        int width,
        int height,
        String status,
        String feedback,
        LocalDateTime createdAt
) {}
