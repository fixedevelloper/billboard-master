package com.cscreativ.billboard.advertiser.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdvertiserVerifiedEvent(UUID advertiserId, LocalDateTime occurredOn) {}
