package com.cscreativ.billboard.owner.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record OwnerRegisteredEvent(UUID ownerId, UUID userId, String companyName, LocalDateTime occurredOn) {}
