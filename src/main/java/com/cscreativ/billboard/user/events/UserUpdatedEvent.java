package com.cscreativ.billboard.user.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserUpdatedEvent(UUID userId, LocalDateTime occurredOn) {}
