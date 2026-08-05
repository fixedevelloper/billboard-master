package com.cscreativ.billboard.installation.api.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record InstallationTaskResponse(
        UUID id,
        UUID campaignId,
        UUID billboardId,
        UUID technicianId,
        LocalDateTime scheduledDate,
        String status,
        String proofPhotoUrl,
        String proofNotes,
        LocalDateTime createdAt
) {}
