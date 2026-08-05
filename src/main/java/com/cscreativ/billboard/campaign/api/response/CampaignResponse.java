package com.cscreativ.billboard.campaign.api.response;

import java.util.UUID;

public record CampaignResponse(
        UUID id,
        UUID bookingId,
        UUID advertiserId,
        String name,
        String description,
        String mediaUrl,
        String fileType,
        String status,
        String rejectionReason
) {}
