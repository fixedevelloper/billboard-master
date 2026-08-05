package com.cscreativ.billboard.security.domain.valueobject;

import java.time.LocalDateTime;
import java.util.Objects;

public class AccessToken {
    private final String token;
    private final LocalDateTime expiresAt;

    public AccessToken(String token, LocalDateTime expiresAt) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Le jeton ne peut pas être vide");
        }
        if (expiresAt == null || expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date d'expiration du jeton est invalide");
        }
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getToken() { return token; }
    public LocalDateTime getExpiresAt() { return expiresAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessToken that = (AccessToken) o;
        return Objects.equals(token, that.token) && Objects.equals(expiresAt, that.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, expiresAt);
    }
}
