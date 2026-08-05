package com.cscreativ.billboard.review.domain.valueobject;

import java.util.Objects;

public class Comment {
    private final String content;

    public Comment(String content) {
        if (content != null && content.length() > 1000) {
            throw new IllegalArgumentException("Le commentaire ne peut pas dépasser 1000 caractères");
        }
        this.content = content != null ? content.trim() : "";
    }

    public String getContent() { return content; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment1 = (Comment) o;
        return Objects.equals(content, comment1.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
