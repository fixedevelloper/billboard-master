package com.cscreativ.billboard.advertiser.domain.exception;

public class AdvertiserNotFoundException extends RuntimeException {
    public AdvertiserNotFoundException(String message) {
        super(message);
    }
}
