package com.cscreativ.billboard.wallet.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface WalletTransactionJpaRepository extends JpaRepository<WalletTransactionEntity, UUID> {
    List<WalletTransactionEntity> findByWalletIdOrderByCreatedAtDesc(UUID walletId);
}
