package com.cscreativ.billboard.admin.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AdminUserJpaRepository extends JpaRepository<AdminUserEntity, UUID> {
    Optional<AdminUserEntity> findByUserId(UUID userId);
}
