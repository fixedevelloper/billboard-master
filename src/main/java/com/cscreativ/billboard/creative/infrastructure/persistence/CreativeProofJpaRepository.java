package com.cscreativ.billboard.creative.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreativeProofJpaRepository extends JpaRepository<CreativeProofEntity, UUID> {
    List<CreativeProofEntity> findByCampaignIdOrderByVersionDesc(UUID campaignId);
    Optional<CreativeProofEntity> findFirstByCampaignIdOrderByVersionDesc(UUID campaignId);
}
