package com.cscreativ.billboard.user.api.response;

public record LoginResponse(String accessToken, String tokenType) {
    public LoginResponse(String accessToken) {
        this(accessToken, "Bearer");
    }
}
