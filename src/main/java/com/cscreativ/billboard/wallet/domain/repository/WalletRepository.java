package com.cscreativ.billboard.wallet.domain.repository;

import com.cscreativ.billboard.wallet.domain.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {
    Wallet save(Wallet wallet);
    Optional<Wallet> findById(UUID id);
    Optional<Wallet> findByUserId(UUID userId);

    /**
     * Verrou pessimiste (SELECT ... FOR UPDATE) : à utiliser uniquement dans une transaction de
     * retrait, pour qu'aucune autre transaction ne puisse lire/modifier ce portefeuille tant que
     * la vérification de solde + le débit n'ont pas été validés (voir WalletOperationService).
     */
    Optional<Wallet> findByUserIdForUpdate(UUID userId);
}
