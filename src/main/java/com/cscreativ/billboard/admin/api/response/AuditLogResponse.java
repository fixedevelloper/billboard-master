package com.cscreativ.billboard.admin.api.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        UUID adminId,
        String action,
        String targetEntity,
        UUID targetId,
        String details,
        LocalDateTime timestamp
) {}
