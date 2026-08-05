package com.cscreativ.billboard.user.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserLoggedInEvent(UUID userId, LocalDateTime occurredOn) {}
