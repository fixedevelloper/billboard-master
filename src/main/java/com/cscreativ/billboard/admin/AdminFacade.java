package com.cscreativ.billboard.admin;

import com.cscreativ.billboard.admin.domain.AdminUser;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AdminFacade {
    Optional<AdminUser> findAdminById(UUID adminId);
    Optional<UUID> findAdminIdByUserId(UUID userId);
    boolean hasRole(UUID adminId, String roleName);

    /**
     * roleNames en String (pas AdminRole) : le type domain n'est pas exposé hors du module.
     * Utilisé par le bootstrap du compte admin (AdminAccountInitializer).
     */
    UUID createAdmin(UUID userId, Set<String> roleNames);
}
