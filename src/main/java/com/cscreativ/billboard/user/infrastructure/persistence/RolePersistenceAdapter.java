package com.cscreativ.billboard.user.infrastructure.persistence;

import com.cscreativ.billboard.user.domain.Permission;
import com.cscreativ.billboard.user.domain.Role;
import com.cscreativ.billboard.user.domain.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RolePersistenceAdapter implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;

    public RolePersistenceAdapter(RoleJpaRepository roleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name).map(this::toDomain);
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return roleJpaRepository.findById(id).map(this::toDomain);
    }

    private Role toDomain(RoleEntity entity) {
        return new Role(
                entity.getId(),
                entity.getName(),
                entity.getPermissions() != null ? entity.getPermissions().stream()
                        .map(p -> new Permission(p.getId(), p.getName())).collect(Collectors.toSet()) : Set.of()
        );
    }
}
