package com.cscreativ.billboard.review.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewApprovedEvent(UUID reviewId, UUID targetId, LocalDateTime occurredOn) {}
