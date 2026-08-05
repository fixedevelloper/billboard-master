package com.cscreativ.billboard.security.api.request;

public record LoginRequest(
        String email,
        String password
) {}
