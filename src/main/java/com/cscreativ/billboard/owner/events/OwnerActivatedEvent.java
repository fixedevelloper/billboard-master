package com.cscreativ.billboard.owner.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record OwnerActivatedEvent(UUID ownerId, LocalDateTime occurredOn) {}
