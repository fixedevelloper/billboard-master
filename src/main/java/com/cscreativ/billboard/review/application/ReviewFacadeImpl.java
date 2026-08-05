package com.cscreativ.billboard.review.application;

import com.cscreativ.billboard.review.ReviewFacade;
import com.cscreativ.billboard.review.domain.BillboardReview;
import com.cscreativ.billboard.review.domain.repository.BillboardReviewRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ReviewFacadeImpl implements ReviewFacade {

    private final BillboardReviewRepository reviewRepository;
    private final ReviewService reviewService;

    public ReviewFacadeImpl(BillboardReviewRepository reviewRepository, ReviewService reviewService) {
        this.reviewRepository = reviewRepository;
        this.reviewService = reviewService;
    }

    @Override
    public Optional<BillboardReview> findReviewById(UUID reviewId) {
        return reviewRepository.findById(reviewId);
    }

    @Override
    public double getAverageRatingForTarget(UUID targetId) {
        return reviewService.calculateAverageRating(targetId);
    }
}
