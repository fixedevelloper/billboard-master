package com.cscreativ.billboard.user.application;

import com.cscreativ.billboard.user.infrastructure.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String generateAccessToken(UUID userId, String email) {
        return jwtTokenProvider.generateToken(userId, email);
    }
}
