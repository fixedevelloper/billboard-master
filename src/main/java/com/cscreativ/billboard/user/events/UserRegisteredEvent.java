package com.cscreativ.billboard.user.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        LocalDateTime occurredOn
) {}
