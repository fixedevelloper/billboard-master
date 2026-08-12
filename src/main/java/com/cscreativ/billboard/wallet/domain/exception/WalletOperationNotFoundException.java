package com.cscreativ.billboard.wallet.domain.exception;

import com.cscreativ.billboard.shared.domain.exception.BusinessException;

public class WalletOperationNotFoundException extends BusinessException {
    public WalletOperationNotFoundException(String message) {
        super(message);
    }
}
