package com.cscreativ.billboard.user.infrastructure.persistence;

import com.cscreativ.billboard.user.domain.Permission;
import com.cscreativ.billboard.user.domain.repository.PermissionRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PermissionPersistenceAdapter implements PermissionRepository {

    private final PermissionJpaRepository permissionJpaRepository;

    public PermissionPersistenceAdapter(PermissionJpaRepository permissionJpaRepository) {
        this.permissionJpaRepository = permissionJpaRepository;
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return permissionJpaRepository.findByName(name).map(this::toDomain);
    }

    @Override
    public Optional<Permission> findById(UUID id) {
        return permissionJpaRepository.findById(id).map(this::toDomain);
    }

    private Permission toDomain(PermissionEntity entity) {
        return new Permission(entity.getId(), entity.getName());
    }
}
