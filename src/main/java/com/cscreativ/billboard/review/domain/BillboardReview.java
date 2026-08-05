package com.cscreativ.billboard.review.domain;

import com.cscreativ.billboard.review.domain.valueobject.Comment;
import com.cscreativ.billboard.review.domain.valueobject.Rating;

import java.time.LocalDateTime;
import java.util.UUID;

public class BillboardReview {
    private UUID id;
    private UUID authorId;
    private UUID targetId; // ID du panneau d'affichage ou du régisseur
    private Rating rating;
    private Comment comment;
    private ReviewStatus status;
    private String moderationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BillboardReview(UUID id, UUID authorId, UUID targetId, Rating rating, Comment comment, ReviewStatus status, String moderationReason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.authorId = authorId;
        this.targetId = targetId;
        this.rating = rating;
        this.comment = comment;
        this.status = status;
        this.moderationReason = moderationReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static BillboardReview create(UUID authorId, UUID targetId, Rating rating, Comment comment) {
        LocalDateTime now = LocalDateTime.now();
        return new BillboardReview(UUID.randomUUID(), authorId, targetId, rating, comment, ReviewStatus.PENDING_MODERATION, null, now, now);
    }

    public void approve() {
        this.status = ReviewStatus.PUBLISHED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(String reason) {
        this.status = ReviewStatus.REJECTED;
        this.moderationReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getAuthorId() { return authorId; }
    public UUID getTargetId() { return targetId; }
    public Rating getRating() { return rating; }
    public Comment getComment() { return comment; }
    public ReviewStatus getStatus() { return status; }
    public String getModerationReason() { return moderationReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
