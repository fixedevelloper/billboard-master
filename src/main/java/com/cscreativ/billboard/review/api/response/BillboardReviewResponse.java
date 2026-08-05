package com.cscreativ.billboard.review.api.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record BillboardReviewResponse(
        UUID id,
        UUID authorId,
        UUID targetId,
        int rating,
        String comment,
        String status,
        String moderationReason,
        LocalDateTime createdAt
) {}
