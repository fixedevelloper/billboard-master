package com.cscreativ.billboard.campaign.domain.repository;

import com.cscreativ.billboard.campaign.domain.Campaign;
import com.cscreativ.billboard.campaign.domain.CampaignStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampaignRepository {
    Campaign save(Campaign campaign);
    Optional<Campaign> findById(UUID id);
    List<Campaign> findByAdvertiserId(UUID advertiserId);
    List<Campaign> findByStatus(CampaignStatus status);
    List<Campaign> findByBookingId(UUID bookingId);
    List<Campaign> findAll();
}
