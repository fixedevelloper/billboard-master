package com.cscreativ.billboard.review.application;

import com.cscreativ.billboard.review.domain.BillboardReview;
import com.cscreativ.billboard.review.domain.ReviewStatus;
import com.cscreativ.billboard.review.domain.exception.ReviewNotFoundException;
import com.cscreativ.billboard.review.domain.repository.BillboardReviewRepository;
import com.cscreativ.billboard.review.domain.valueobject.Comment;
import com.cscreativ.billboard.review.domain.valueobject.Rating;
import com.cscreativ.billboard.review.events.ReviewApprovedEvent;
import com.cscreativ.billboard.review.events.ReviewSubmittedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    private final BillboardReviewRepository reviewRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReviewService(BillboardReviewRepository reviewRepository, ApplicationEventPublisher eventPublisher) {
        this.reviewRepository = reviewRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public BillboardReview submitReview(UUID authorId, UUID targetId, int ratingValue, String commentContent) {
        Rating rating = new Rating(ratingValue);
        Comment comment = new Comment(commentContent);

        BillboardReview review = BillboardReview.create(authorId, targetId, rating, comment);
        BillboardReview saved = reviewRepository.save(review);

        eventPublisher.publishEvent(new ReviewSubmittedEvent(saved.getId(), saved.getAuthorId(), saved.getTargetId(), saved.getRating().getValue(), LocalDateTime.now()));
        return saved;
    }

    @Transactional
    public void approveReview(UUID reviewId) {
        BillboardReview review = getReviewById(reviewId);
        review.approve();
        BillboardReview saved = reviewRepository.save(review);

        eventPublisher.publishEvent(new ReviewApprovedEvent(saved.getId(), saved.getTargetId(), LocalDateTime.now()));
    }

    @Transactional
    public void rejectReview(UUID reviewId, String reason) {
        BillboardReview review = getReviewById(reviewId);
        review.reject(reason);
        reviewRepository.save(review);
    }

    public BillboardReview getReviewById(UUID id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Avis non trouvé avec l'id : " + id));
    }

    public List<BillboardReview> getPublishedReviewsForTarget(UUID targetId) {
        return reviewRepository.findByTargetIdAndStatus(targetId, ReviewStatus.PUBLISHED);
    }

    public double calculateAverageRating(UUID targetId) {
        List<BillboardReview> reviews = getPublishedReviewsForTarget(targetId);
        return reviews.stream()
                .mapToInt(r -> r.getRating().getValue())
                .average()
                .orElse(0.0);
    }
}
