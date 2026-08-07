package com.cscreativ.billboard.billboard.api.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record BillboardImageResponse(
        UUID id,
        UUID billboardId,
        String url,
        LocalDateTime createdAt
) {}
