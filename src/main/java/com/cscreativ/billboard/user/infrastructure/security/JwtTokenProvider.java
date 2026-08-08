package com.cscreativ.billboard.user.infrastructure.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class    JwtTokenProvider {

    /**
     * Nom du cookie HttpOnly qui porte le JWT. Le frontend ne doit jamais avoir besoin de le
     * lire lui-même (ni via document.cookie, ni via localStorage) : c'est tout l'intérêt de
     * passer par un cookie HttpOnly plutôt que par le corps de la réponse / un header Authorization.
     */
    public static final String COOKIE_NAME = "ACCESS_TOKEN";

    private final SecretKey key;
    private final long expirationMs;
    private final boolean cookieSecure;
    private final String cookieSameSite;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                             @Value("${jwt.expiration-ms:3600000}") long expirationMs,
                             @Value("${jwt.cookie-secure:true}") boolean cookieSecure,
                             @Value("${jwt.cookie-same-site:Lax}") String cookieSameSite) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
    }

    public ResponseCookie buildAuthCookie(String token) {
        return ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(Duration.ofMillis(expirationMs))
                .build();
    }

    public ResponseCookie clearAuthCookie() {
        return ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(0)
                .build();
    }

    public String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
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

    public String getClaim(String token, String claimName) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get(claimName, String.class);
    }
}
