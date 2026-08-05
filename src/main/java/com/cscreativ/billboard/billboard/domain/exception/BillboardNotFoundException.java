package com.cscreativ.billboard.billboard.domain.exception;

public class BillboardNotFoundException extends RuntimeException {
    public BillboardNotFoundException(String message) {
        super(message);
    }
}
