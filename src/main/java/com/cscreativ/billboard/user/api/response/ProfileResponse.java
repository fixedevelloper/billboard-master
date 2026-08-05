package com.cscreativ.billboard.user.api.response;

import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String email,
        String fullName,
        String phoneNumber,
        String status
) {}
