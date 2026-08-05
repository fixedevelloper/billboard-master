package com.cscreativ.billboard.campaign.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record CampaignApprovedEvent(UUID campaignId, UUID bookingId, LocalDateTime occurredOn) {}
