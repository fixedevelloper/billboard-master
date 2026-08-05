package com.cscreativ.billboard.storage.api.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record StoredFileResponse(
        UUID id,
        String originalFilename,
        String contentType,
        long size,
        String publicUrl,
        UUID ownerId,
        LocalDateTime uploadedAt
) {}
