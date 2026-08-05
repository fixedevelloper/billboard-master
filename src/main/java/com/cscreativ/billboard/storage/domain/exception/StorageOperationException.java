package com.cscreativ.billboard.storage.domain.exception;

public class StorageOperationException extends RuntimeException {
    public StorageOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
