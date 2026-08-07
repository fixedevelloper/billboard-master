package com.cscreativ.billboard.wallet.domain.repository;

import com.cscreativ.billboard.wallet.domain.WalletTransaction;

import java.util.List;
import java.util.UUID;

public interface WalletTransactionRepository {
    WalletTransaction save(WalletTransaction transaction);
    List<WalletTransaction> findByWalletId(UUID walletId);
}
