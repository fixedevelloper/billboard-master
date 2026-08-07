package com.cscreativ.billboard.user.application;

import com.cscreativ.billboard.user.infrastructure.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String generateAccessToken(UUID userId, String email, Map<String, String> extraClaims) {
        return jwtTokenProvider.generateToken(userId, email, extraClaims);
    }
}
