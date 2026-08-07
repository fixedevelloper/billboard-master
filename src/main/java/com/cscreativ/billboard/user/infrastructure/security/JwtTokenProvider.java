package com.cscreativ.billboard.user.infrastructure.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                             @Value("${jwt.expiration-ms:3600000}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(UUID userId, String email, Map<String, String> extraClaims) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        var builder = Jwts.builder()
                .subject(email)
                .claim("userId", userId.toString())
                .issuedAt(now)
                .expiration(expiry);
        extraClaims.forEach(builder::claim);
        return builder.signWith(key).compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public UUID getUserIdFromToken(String token) {
        String userId = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("userId", String.class);
        return UUID.fromString(userId);
    }
}
