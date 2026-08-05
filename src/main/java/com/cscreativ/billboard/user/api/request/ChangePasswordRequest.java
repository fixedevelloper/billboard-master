package com.cscreativ.billboard.user.api.request;

public record ChangePasswordRequest(String oldPassword, String newPassword) {}
