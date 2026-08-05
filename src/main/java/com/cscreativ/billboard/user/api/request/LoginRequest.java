package com.cscreativ.billboard.user.api.request;

public record LoginRequest(
        String email,
        String password
) {}
