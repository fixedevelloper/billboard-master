package com.cscreativ.billboard.campaign.application;

import com.cscreativ.billboard.campaign.CampaignFacade;
import com.cscreativ.billboard.campaign.domain.Campaign;
import com.cscreativ.billboard.campaign.domain.CampaignStatus;
import com.cscreativ.billboard.campaign.domain.repository.CampaignRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CampaignFacadeImpl implements CampaignFacade {

    private final CampaignRepository campaignRepository;

    public CampaignFacadeImpl(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @Override
    public Optional<Campaign> findCampaignById(UUID campaignId) {
        return campaignRepository.findById(campaignId);
    }

    @Override
    public boolean isApproved(UUID campaignId) {
        return campaignRepository.findById(campaignId)
                .map(campaign -> campaign.getStatus() == CampaignStatus.APPROVED)
                .orElse(false);
    }

    @Override
    public List<UUID> findCampaignIdsByBooking(UUID bookingId) {
        return campaignRepository.findByBookingId(bookingId).stream()
                .map(Campaign::getId)
                .toList();
    }
}
