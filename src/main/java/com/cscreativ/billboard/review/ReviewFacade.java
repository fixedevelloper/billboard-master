package com.cscreativ.billboard.review;

import com.cscreativ.billboard.review.domain.BillboardReview;

import java.util.Optional;
import java.util.UUID;

public interface ReviewFacade {
    Optional<BillboardReview> findReviewById(UUID reviewId);
    double getAverageRatingForTarget(UUID targetId);
}
