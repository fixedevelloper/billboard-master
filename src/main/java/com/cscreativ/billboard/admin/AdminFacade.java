package com.cscreativ.billboard.admin;

import com.cscreativ.billboard.admin.domain.AdminUser;

import java.util.Optional;
import java.util.UUID;

public interface AdminFacade {
    Optional<AdminUser> findAdminById(UUID adminId);
    boolean hasRole(UUID adminId, String roleName);
}
