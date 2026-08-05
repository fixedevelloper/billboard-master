package com.cscreativ.billboard.mediabuyer.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record MediaBuyerRegisteredEvent(UUID buyerId, UUID userId, String companyName, LocalDateTime occurredOn) {}
