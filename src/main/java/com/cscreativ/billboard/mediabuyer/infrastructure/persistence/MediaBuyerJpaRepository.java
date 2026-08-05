package com.cscreativ.billboard.mediabuyer.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MediaBuyerJpaRepository extends JpaRepository<MediaBuyerEntity, UUID> {
    Optional<MediaBuyerEntity> findByUserId(UUID userId);
}
