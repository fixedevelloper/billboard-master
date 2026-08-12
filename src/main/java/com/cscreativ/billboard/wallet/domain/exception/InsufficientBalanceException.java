package com.cscreativ.billboard.wallet.domain.exception;

import com.cscreativ.billboard.shared.domain.exception.BusinessException;

public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
