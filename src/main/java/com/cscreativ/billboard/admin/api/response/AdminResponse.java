package com.cscreativ.billboard.admin.api.response;

import java.util.Set;
import java.util.UUID;

public record AdminResponse(
        UUID id,
        UUID userId,
        Set<String> roles,
        boolean active
) {}
