package com.cscreativ.billboard.review.infrastructure.persistence;

import com.cscreativ.billboard.review.domain.BillboardReview;
import com.cscreativ.billboard.review.domain.ReviewStatus;
import com.cscreativ.billboard.review.domain.repository.BillboardReviewRepository;
import com.cscreativ.billboard.review.domain.valueobject.Comment;
import com.cscreativ.billboard.review.domain.valueobject.Rating;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BillboardReviewPersistenceAdapter implements BillboardReviewRepository {

    private final BillboardReviewJpaRepository jpaRepository;

    public BillboardReviewPersistenceAdapter(BillboardReviewJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public BillboardReview save(BillboardReview review) {
        BillboardReviewEntity entity = toEntity(review);
        BillboardReviewEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<BillboardReview> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<BillboardReview> findByTargetIdAndStatus(UUID targetId, ReviewStatus status) {
        return jpaRepository.findByTargetIdAndStatus(targetId, status).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<BillboardReview> findByAuthorId(UUID authorId) {
        return jpaRepository.findByAuthorId(authorId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<BillboardReview> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private BillboardReviewEntity toEntity(BillboardReview domain) {
        BillboardReviewEntity entity = new BillboardReviewEntity();
        entity.setId(domain.getId());
        entity.setAuthorId(domain.getAuthorId());
        entity.setTargetId(domain.getTargetId());
        entity.setRating(domain.getRating().getValue());
        entity.setComment(domain.getComment().getContent());
        entity.setStatus(domain.getStatus());
        entity.setModerationReason(domain.getModerationReason());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private BillboardReview toDomain(BillboardReviewEntity entity) {
        Rating rating = new Rating(entity.getRating());
        Comment comment = new Comment(entity.getComment());

        return new BillboardReview(
                entity.getId(),
                entity.getAuthorId(),
                entity.getTargetId(),
                rating,
                comment,
                entity.getStatus(),
                entity.getModerationReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
