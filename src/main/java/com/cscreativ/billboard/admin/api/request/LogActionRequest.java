package com.cscreativ.billboard.admin.api.request;

import com.cscreativ.billboard.admin.domain.AuditAction;

import java.util.UUID;

public record LogActionRequest(
        AuditAction action,
        String targetEntity,
        UUID targetId,
        String details
) {}
