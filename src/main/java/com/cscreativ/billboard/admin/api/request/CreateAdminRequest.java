package com.cscreativ.billboard.admin.api.request;

import com.cscreativ.billboard.admin.domain.AdminRole;

import java.util.Set;
import java.util.UUID;

public record CreateAdminRequest(
        UUID userId,
        Set<AdminRole> roles
) {}
