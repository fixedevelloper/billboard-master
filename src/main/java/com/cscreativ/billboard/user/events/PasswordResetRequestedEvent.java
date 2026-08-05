package com.cscreativ.billboard.user.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record PasswordResetRequestedEvent(UUID userId, String email, String resetToken, LocalDateTime occurredOn) {}
