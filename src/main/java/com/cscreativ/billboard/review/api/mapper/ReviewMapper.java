package com.cscreativ.billboard.review.api.mapper;

import com.cscreativ.billboard.review.api.response.BillboardReviewResponse;
import com.cscreativ.billboard.review.domain.BillboardReview;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public BillboardReviewResponse toResponse(BillboardReview review) {
        return new BillboardReviewResponse(
                review.getId(),
                review.getAuthorId(),
                review.getTargetId(),
                review.getRating().getValue(),
                review.getComment().getContent(),
                review.getStatus().name(),
                review.getModerationReason(),
                review.getCreatedAt()
        );
    }
}
