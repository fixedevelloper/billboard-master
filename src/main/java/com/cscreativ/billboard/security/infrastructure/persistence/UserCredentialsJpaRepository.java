package com.cscreativ.billboard.security.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserCredentialsJpaRepository extends JpaRepository<UserCredentialsEntity, UUID> {
    Optional<UserCredentialsEntity> findByEmail(String email);
    Optional<UserCredentialsEntity> findByUserId(UUID userId);
}
