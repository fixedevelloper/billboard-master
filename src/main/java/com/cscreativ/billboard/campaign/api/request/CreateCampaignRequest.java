package com.cscreativ.billboard.campaign.api.request;

import java.util.UUID;

public record CreateCampaignRequest(
        UUID bookingId,
        UUID advertiserId,
        String name,
        String description,
        String mediaUrl,
        String fileType,
        long fileSize
) {}
