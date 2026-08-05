package com.cscreativ.billboard.security.api.request;

import com.cscreativ.billboard.security.domain.UserRole;

import java.util.Set;
import java.util.UUID;

public record RegisterCredentialsRequest(
        UUID userId,
        String email,
        String password,
        Set<UserRole> roles
) {}
