package com.cscreativ.billboard.review.api;

import com.cscreativ.billboard.review.api.mapper.ReviewMapper;
import com.cscreativ.billboard.review.api.request.RejectReviewRequest;
import com.cscreativ.billboard.review.api.request.SubmitReviewRequest;
import com.cscreativ.billboard.review.api.response.BillboardReviewResponse;
import com.cscreativ.billboard.review.application.ReviewService;
import com.cscreativ.billboard.review.domain.BillboardReview;
import com.cscreativ.billboard.shared.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    public ReviewController(ReviewService reviewService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    @PostMapping
    public ResponseEntity<BillboardReviewResponse> submitReview(@RequestBody SubmitReviewRequest request,
                                                                 @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.require(currentUser.isAdmin() || currentUser.isOneOfMine(request.authorId()));
        BillboardReview review = reviewService.submitReview(
                request.authorId(),
                request.targetId(),
                request.rating(),
                request.comment()
        );
        return ResponseEntity.ok(reviewMapper.toResponse(review));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Void> approveReview(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        reviewService.approveReview(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Void> rejectReview(@PathVariable UUID id, @RequestBody RejectReviewRequest request,
                                              @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        reviewService.rejectReview(id, request.reason());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillboardReviewResponse> getReviewById(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        BillboardReview review = reviewService.getReviewById(id);
        currentUser.require(currentUser.isAdmin() || currentUser.isOneOfMine(review.getAuthorId()));
        return ResponseEntity.ok(reviewMapper.toResponse(review));
    }

    @GetMapping("/target/{targetId}")
    public ResponseEntity<List<BillboardReviewResponse>> getPublishedReviewsForTarget(@PathVariable UUID targetId) {
        List<BillboardReview> reviews = reviewService.getPublishedReviewsForTarget(targetId);
        return ResponseEntity.ok(reviews.stream().map(reviewMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/target/{targetId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable UUID targetId) {
        double average = reviewService.calculateAverageRating(targetId);
        return ResponseEntity.ok(average);
    }

    @GetMapping
    public ResponseEntity<List<BillboardReviewResponse>> getAllReviews(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        List<BillboardReview> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews.stream().map(reviewMapper::toResponse).collect(Collectors.toList()));
    }
}
