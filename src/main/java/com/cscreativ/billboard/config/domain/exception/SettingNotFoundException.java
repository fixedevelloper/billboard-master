package com.cscreativ.billboard.config.domain.exception;

public class SettingNotFoundException extends RuntimeException {
    public SettingNotFoundException(String message) {
        super(message);
    }
}
