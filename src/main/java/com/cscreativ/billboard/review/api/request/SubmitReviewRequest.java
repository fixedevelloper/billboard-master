package com.cscreativ.billboard.review.api.request;

import java.util.UUID;

public record SubmitReviewRequest(
        UUID authorId,
        UUID targetId,
        int rating,
        String comment
) {}
