package com.cscreativ.billboard.user.domain.repository;

import com.cscreativ.billboard.user.domain.Permission;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository {
    Optional<Permission> findByName(String name);
    Optional<Permission> findById(UUID id);
}
