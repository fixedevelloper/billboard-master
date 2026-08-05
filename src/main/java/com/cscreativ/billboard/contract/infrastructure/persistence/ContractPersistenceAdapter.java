package com.cscreativ.billboard.contract.infrastructure.persistence;

import com.cscreativ.billboard.contract.domain.Contract;
import com.cscreativ.billboard.contract.domain.repository.ContractRepository;
import com.cscreativ.billboard.contract.domain.valueobject.SignatureDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ContractPersistenceAdapter implements ContractRepository {

    private final ContractJpaRepository jpaRepository;

    public ContractPersistenceAdapter(ContractJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Contract save(Contract contract) {
        ContractEntity entity = toEntity(contract);
        ContractEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Contract> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Contract> findByBookingId(UUID bookingId) {
        return jpaRepository.findByBookingId(bookingId).map(this::toDomain);
    }

    @Override
    public List<Contract> findByOwnerId(UUID ownerId) {
        return jpaRepository.findByOwnerId(ownerId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Contract> findByAdvertiserId(UUID advertiserId) {
        return jpaRepository.findByAdvertiserId(advertiserId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    private ContractEntity toEntity(Contract domain) {
        ContractEntity entity = new ContractEntity();
        entity.setId(domain.getId());
        entity.setBookingId(domain.getBookingId());
        entity.setOwnerId(domain.getOwnerId());
        entity.setAdvertiserId(domain.getAdvertiserId());
        entity.setTermsAndConditions(domain.getTermsAndConditions());
        entity.setStatus(domain.getStatus());

        if (domain.getOwnerSignature() != null) {
            entity.setOwnerSignedBy(domain.getOwnerSignature().getSignedBy());
            entity.setOwnerSignedIp(domain.getOwnerSignature().getIpAddress());
            entity.setOwnerSignedAt(domain.getOwnerSignature().getSignedAt());
        }

        if (domain.getAdvertiserSignature() != null) {
            entity.setAdvertiserSignedBy(domain.getAdvertiserSignature().getSignedBy());
            entity.setAdvertiserSignedIp(domain.getAdvertiserSignature().getIpAddress());
            entity.setAdvertiserSignedAt(domain.getAdvertiserSignature().getSignedAt());
        }

        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private Contract toDomain(ContractEntity entity) {
        SignatureDetails ownerSig = entity.getOwnerSignedBy() != null ?
                new SignatureDetails(entity.getOwnerSignedBy(), entity.getOwnerSignedIp(), entity.getOwnerSignedAt()) : null;

        SignatureDetails advertiserSig = entity.getAdvertiserSignedBy() != null ?
                new SignatureDetails(entity.getAdvertiserSignedBy(), entity.getAdvertiserSignedIp(), entity.getAdvertiserSignedAt()) : null;

        return new Contract(
                entity.getId(),
                entity.getBookingId(),
                entity.getOwnerId(),
                entity.getAdvertiserId(),
                entity.getTermsAndConditions(),
                entity.getStatus(),
                ownerSig,
                advertiserSig,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
