package com.cscreativ.billboard.campaign.api.mapper;

import com.cscreativ.billboard.campaign.api.response.CampaignResponse;
import com.cscreativ.billboard.campaign.domain.Campaign;
import org.springframework.stereotype.Component;

@Component
public class CampaignMapper {

    public CampaignResponse toResponse(Campaign campaign) {
        return new CampaignResponse(
                campaign.getId(),
                campaign.getBookingId(),
                campaign.getAdvertiserId(),
                campaign.getName(),
                campaign.getDescription(),
                campaign.getMediaAsset().getUrl(),
                campaign.getMediaAsset().getFileType(),
                campaign.getStatus().name(),
                campaign.getRejectionReason()
        );
    }
}
