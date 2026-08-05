package com.cscreativ.billboard.owner.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BillboardOwnerJpaRepository extends JpaRepository<BillboardOwnerEntity, UUID> {
    Optional<BillboardOwnerEntity> findByUserId(UUID userId);
}
