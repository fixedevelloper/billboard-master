package com.cscreativ.billboard.wallet.infrastructure.persistence;

import com.cscreativ.billboard.wallet.domain.WalletTransaction;
import com.cscreativ.billboard.wallet.domain.WalletTransactionType;
import com.cscreativ.billboard.wallet.domain.repository.WalletTransactionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class WalletTransactionPersistenceAdapter implements WalletTransactionRepository {

    private final WalletTransactionJpaRepository jpaRepository;

    public WalletTransactionPersistenceAdapter(WalletTransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WalletTransaction save(WalletTransaction transaction) {
        WalletTransactionEntity entity = toEntity(transaction);
        WalletTransactionEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<WalletTransaction> findByWalletId(UUID walletId) {
        return jpaRepository.findByWalletIdOrderByCreatedAtDesc(walletId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByWalletIdAndTypeAndReference(UUID walletId, WalletTransactionType type, String reference) {
        return jpaRepository.existsByWalletIdAndTypeAndReference(walletId, type, reference);
    }

    private WalletTransactionEntity toEntity(WalletTransaction domain) {
        WalletTransactionEntity entity = new WalletTransactionEntity();
        entity.setId(domain.getId());
        entity.setWalletId(domain.getWalletId());
        entity.setType(domain.getType());
        entity.setAmount(domain.getAmount());
        entity.setCurrency(domain.getCurrency());
        entity.setReference(domain.getReference());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private WalletTransaction toDomain(WalletTransactionEntity entity) {
        return new WalletTransaction(
                entity.getId(),
                entity.getWalletId(),
                entity.getType(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getReference(),
                entity.getCreatedAt()
        );
    }
}
