package com.cscreativ.billboard.review.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewSubmittedEvent(UUID reviewId, UUID authorId, UUID targetId, int rating, LocalDateTime occurredOn) {}
