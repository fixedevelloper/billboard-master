package com.cscreativ.billboard.platformsettings.domain.exception;

public class SettingNotFoundException extends RuntimeException {
    public SettingNotFoundException(String message) {
        super(message);
    }
}
