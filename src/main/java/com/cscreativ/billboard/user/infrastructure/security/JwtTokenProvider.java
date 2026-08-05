package com.cscreativ.billboard.user.infrastructure.security;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    public String generateToken(UUID userId, String email) {
        return "mocked-jwt-token-for-" + email;
    }

    public boolean validateToken(String token) {
        return token != null && token.startsWith("mocked-jwt-token-for-");
    }

    public String getEmailFromToken(String token) {
        return token.replace("mocked-jwt-token-for-", "");
    }
}
