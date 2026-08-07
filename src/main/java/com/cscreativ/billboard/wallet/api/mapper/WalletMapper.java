package com.cscreativ.billboard.wallet.api.mapper;

import com.cscreativ.billboard.wallet.api.response.WalletResponse;
import com.cscreativ.billboard.wallet.api.response.WalletTransactionResponse;
import com.cscreativ.billboard.wallet.domain.Wallet;
import com.cscreativ.billboard.wallet.domain.WalletTransaction;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public WalletResponse toResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.getUpdatedAt()
        );
    }

    public WalletTransactionResponse toResponse(WalletTransaction transaction) {
        return new WalletTransactionResponse(
                transaction.getId(),
                transaction.getWalletId(),
                transaction.getType().name(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getReference(),
                transaction.getCreatedAt()
        );
    }
}
