package com.cscreativ.billboard.wallet.infrastructure.persistence;

import com.cscreativ.billboard.wallet.domain.Wallet;
import com.cscreativ.billboard.wallet.domain.repository.WalletRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class WalletPersistenceAdapter implements WalletRepository {

    private final WalletJpaRepository jpaRepository;

    public WalletPersistenceAdapter(WalletJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Wallet save(Wallet wallet) {
        WalletEntity entity = toEntity(wallet);
        WalletEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Wallet> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Wallet> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    private WalletEntity toEntity(Wallet domain) {
        WalletEntity entity = new WalletEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setBalance(domain.getBalance());
        entity.setCurrency(domain.getCurrency());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private Wallet toDomain(WalletEntity entity) {
        return new Wallet(
                entity.getId(),
                entity.getUserId(),
                entity.getBalance(),
                entity.getCurrency(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
