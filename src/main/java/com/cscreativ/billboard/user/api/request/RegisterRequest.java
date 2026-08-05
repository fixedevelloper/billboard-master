package com.cscreativ.billboard.user.api.request;

public record RegisterRequest(
        String email,
        String password,
        String firstName,
        String lastName,
        String phoneNumber
) {}
