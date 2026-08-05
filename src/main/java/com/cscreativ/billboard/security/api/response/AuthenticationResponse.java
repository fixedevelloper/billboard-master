package com.cscreativ.billboard.security.api.response;

import java.time.LocalDateTime;

public record AuthenticationResponse(
        String token,
        LocalDateTime expiresAt
) {}
