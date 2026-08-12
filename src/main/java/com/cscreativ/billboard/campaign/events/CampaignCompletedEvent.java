package com.cscreativ.billboard.campaign.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record CampaignCompletedEvent(UUID campaignId, UUID bookingId, LocalDateTime occurredOn) {}
