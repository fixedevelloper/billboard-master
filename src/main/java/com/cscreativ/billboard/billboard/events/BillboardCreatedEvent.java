package com.cscreativ.billboard.billboard.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record BillboardCreatedEvent(UUID billboardId, UUID ownerId, LocalDateTime occurredOn) {}
