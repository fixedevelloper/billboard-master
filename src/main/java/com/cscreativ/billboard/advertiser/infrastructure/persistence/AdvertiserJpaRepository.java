package com.cscreativ.billboard.advertiser.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AdvertiserJpaRepository extends JpaRepository<AdvertiserEntity, UUID> {
    Optional<AdvertiserEntity> findByUserId(UUID userId);
    boolean existsByTaxNumber(String taxNumber);
}
