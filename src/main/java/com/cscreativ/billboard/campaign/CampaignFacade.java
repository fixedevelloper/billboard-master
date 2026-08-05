package com.cscreativ.billboard.campaign;

import com.cscreativ.billboard.campaign.domain.Campaign;

import java.util.Optional;
import java.util.UUID;

public interface CampaignFacade {
    Optional<Campaign> findCampaignById(UUID campaignId);
    boolean isApproved(UUID campaignId);
}
