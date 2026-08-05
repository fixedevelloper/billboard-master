package com.cscreativ.billboard.security;

import java.util.Optional;
import java.util.UUID;

public interface SecurityFacade {
    Optional<UUID> getCurrentUserId();
    boolean isAuthenticated();
    boolean hasRole(String role);
}
