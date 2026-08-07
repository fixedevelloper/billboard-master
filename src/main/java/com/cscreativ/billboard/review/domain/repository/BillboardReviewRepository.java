package com.cscreativ.billboard.review.domain.repository;

import com.cscreativ.billboard.review.domain.BillboardReview;
import com.cscreativ.billboard.review.domain.ReviewStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillboardReviewRepository {
    BillboardReview save(BillboardReview review);
    Optional<BillboardReview> findById(UUID id);
    List<BillboardReview> findByTargetIdAndStatus(UUID targetId, ReviewStatus status);
    List<BillboardReview> findByAuthorId(UUID authorId);
    List<BillboardReview> findAll();
}
