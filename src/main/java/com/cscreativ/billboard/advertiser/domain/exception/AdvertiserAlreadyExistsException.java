package com.cscreativ.billboard.advertiser.domain.exception;

public class AdvertiserAlreadyExistsException extends RuntimeException {
    public AdvertiserAlreadyExistsException(String message) {
        super(message);
    }
}
