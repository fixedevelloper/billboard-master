package com.cscreativ.billboard.user.api.request;

public record ResetPasswordRequest(String token, String newPassword) {}
