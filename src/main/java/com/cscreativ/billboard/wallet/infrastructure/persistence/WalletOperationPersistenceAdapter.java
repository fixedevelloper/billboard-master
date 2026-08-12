package com.cscreativ.billboard.wallet.infrastructure.persistence;

import com.cscreativ.billboard.wallet.domain.WalletOperation;
import com.cscreativ.billboard.wallet.domain.repository.WalletOperationRepository;
import com.cscreativ.billboard.wallet.domain.valueobject.BankDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class WalletOperationPersistenceAdapter implements WalletOperationRepository {

    private final WalletOperationJpaRepository jpaRepository;

    public WalletOperationPersistenceAdapter(WalletOperationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WalletOperation save(WalletOperation operation) {
        WalletOperationEntity entity = toEntity(operation);
        WalletOperationEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<WalletOperation> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<WalletOperation> findByUserIdOrderByCreatedAtDesc(UUID userId) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<WalletOperation> findAllByOrderByCreatedAtDesc() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private WalletOperationEntity toEntity(WalletOperation domain) {
        WalletOperationEntity entity = new WalletOperationEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setType(domain.getType());
        entity.setMethod(domain.getMethod());
        entity.setStatus(domain.getStatus());
        entity.setAmount(domain.getAmount());
        entity.setCurrency(domain.getCurrency());
        entity.setPhoneNumber(domain.getPhoneNumber());
        BankDetails bankDetails = domain.getBankDetails();
        if (bankDetails != null) {
            entity.setBankAccountHolder(bankDetails.getAccountHolderName());
            entity.setBankIban(bankDetails.getIban());
            entity.setBankName(bankDetails.getBankName());
        }
        entity.setReference(domain.getReference());
        entity.setFailureReason(domain.getFailureReason());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private WalletOperation toDomain(WalletOperationEntity entity) {
        BankDetails bankDetails = entity.getBankIban() != null
                ? new BankDetails(entity.getBankAccountHolder(), entity.getBankIban(), entity.getBankName())
                : null;
        return new WalletOperation(
                entity.getId(),
                entity.getUserId(),
                entity.getType(),
                entity.getMethod(),
                entity.getStatus(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getPhoneNumber(),
                bankDetails,
                entity.getReference(),
                entity.getFailureReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
