package com.cscreativ.billboard.advertiser.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdvertiserRegisteredEvent(UUID advertiserId, UUID userId, String companyName, LocalDateTime occurredOn) {}
