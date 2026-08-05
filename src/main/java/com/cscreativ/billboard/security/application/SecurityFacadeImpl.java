package com.cscreativ.billboard.security.application;

import com.cscreativ.billboard.security.SecurityFacade;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SecurityFacadeImpl implements SecurityFacade {

    @Override
    public Optional<UUID> getCurrentUserId() {
        // Mock simple de récupération du ContextHolder Spring Security
        return Optional.of(UUID.randomUUID());
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public boolean hasRole(String role) {
        return true;
    }
}
