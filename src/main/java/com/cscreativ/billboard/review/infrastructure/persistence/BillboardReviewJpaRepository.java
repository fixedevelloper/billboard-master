package com.cscreativ.billboard.review.infrastructure.persistence;

import com.cscreativ.billboard.review.domain.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BillboardReviewJpaRepository extends JpaRepository<BillboardReviewEntity, UUID> {
    List<BillboardReviewEntity> findByTargetIdAndStatus(UUID targetId, ReviewStatus status);
    List<BillboardReviewEntity> findByAuthorId(UUID authorId);
}
