package com.cscreativ.billboard.wallet.domain.repository;

import com.cscreativ.billboard.wallet.domain.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {
    Wallet save(Wallet wallet);
    Optional<Wallet> findById(UUID id);
    Optional<Wallet> findByUserId(UUID userId);
}
