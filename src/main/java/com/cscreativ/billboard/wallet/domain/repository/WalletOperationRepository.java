package com.cscreativ.billboard.wallet.domain.repository;

import com.cscreativ.billboard.wallet.domain.WalletOperation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletOperationRepository {
    WalletOperation save(WalletOperation operation);
    Optional<WalletOperation> findById(UUID id);
    List<WalletOperation> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<WalletOperation> findAllByOrderByCreatedAtDesc();
}
