package com.cscreativ.billboard.creative;

import com.cscreativ.billboard.creative.domain.CreativeProof;

import java.util.Optional;
import java.util.UUID;

public interface CreativeFacade {
    Optional<CreativeProof> findLatestProofByCampaignId(UUID campaignId);
    boolean isCreativeApproved(UUID campaignId);
}
