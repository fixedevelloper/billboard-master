package com.cscreativ.billboard.user.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDisabledEvent(UUID userId, LocalDateTime occurredOn) {}
