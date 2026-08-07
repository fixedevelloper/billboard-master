package com.cscreativ.billboard.user.api.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AdminUserResponse(
        UUID id,
        String email,
        String fullName,
        String phoneNumber,
        String status,
        List<String> roles,
        LocalDateTime createdAt
) {}
