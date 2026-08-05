package com.cscreativ.billboard.user.domain.repository;

import com.cscreativ.billboard.user.domain.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {
    Optional<Role> findByName(String name);
    Optional<Role> findById(UUID id);
}
