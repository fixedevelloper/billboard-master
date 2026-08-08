package com.cscreativ.billboard.campaign;

import com.cscreativ.billboard.campaign.domain.Campaign;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampaignFacade {
    Optional<Campaign> findCampaignById(UUID campaignId);
    boolean isApproved(UUID campaignId);
    List<UUID> findCampaignIdsByBooking(UUID bookingId);
    Optional<UUID> findAdvertiserIdByCampaign(UUID campaignId);
    Optional<UUID> findBookingIdByCampaign(UUID campaignId);
}
