package com.cscreativ.billboard.storage.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record FileDeletedEvent(UUID fileId, UUID ownerId, LocalDateTime occurredOn) {}
