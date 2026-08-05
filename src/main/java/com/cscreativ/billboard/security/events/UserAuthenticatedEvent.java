package com.cscreativ.billboard.security.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserAuthenticatedEvent(UUID userId, String email, LocalDateTime occurredOn) {}
