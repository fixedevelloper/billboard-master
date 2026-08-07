package com.cscreativ.billboard.campaign.api.mapper;

import com.cscreativ.billboard.campaign.api.response.CampaignResponse;
import com.cscreativ.billboard.campaign.domain.Campaign;
import org.springframework.stereotype.Component;

@Component
public class CampaignMapper {

    public CampaignResponse toResponse(Campaign campaign) {
        var mediaAsset = campaign.getMediaAsset();
        return new CampaignResponse(
                campaign.getId(),
                campaign.getBookingId(),
                campaign.getAdvertiserId(),
                campaign.getName(),
                campaign.getDescription(),
                mediaAsset == null ? null : mediaAsset.getUrl(),
                mediaAsset == null ? null : mediaAsset.getFileType(),
                campaign.getStatus().name(),
                campaign.getRejectionReason()
        );
    }
}
