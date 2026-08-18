package com.cscreativ.billboard.campaign.api.request;

import java.util.UUID;

public record CreateCampaignRequest(
        UUID bookingId,
        UUID advertiserId,
        String name,
        String description,
        UUID mediaFileId,
        String fileType,
        Long fileSize
) {}
