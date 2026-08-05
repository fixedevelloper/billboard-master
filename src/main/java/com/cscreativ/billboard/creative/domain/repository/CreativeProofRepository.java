package com.cscreativ.billboard.creative.domain.repository;

import com.cscreativ.billboard.creative.domain.CreativeProof;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreativeProofRepository {
    CreativeProof save(CreativeProof proof);
    Optional<CreativeProof> findById(UUID id);
    List<CreativeProof> findByCampaignId(UUID campaignId);
    Optional<CreativeProof> findLatestByCampaignId(UUID campaignId);
}
