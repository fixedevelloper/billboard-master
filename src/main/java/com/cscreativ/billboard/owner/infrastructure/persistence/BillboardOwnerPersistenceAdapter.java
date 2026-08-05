package com.cscreativ.billboard.owner.infrastructure.persistence;

import com.cscreativ.billboard.owner.domain.BillboardOwner;
import com.cscreativ.billboard.owner.domain.repository.BillboardOwnerRepository;
import com.cscreativ.billboard.owner.domain.valueobject.OwnerDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BillboardOwnerPersistenceAdapter implements BillboardOwnerRepository {

    private final BillboardOwnerJpaRepository jpaRepository;

    public BillboardOwnerPersistenceAdapter(BillboardOwnerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public BillboardOwner save(BillboardOwner owner) {
        BillboardOwnerEntity entity = toEntity(owner);
        BillboardOwnerEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<BillboardOwner> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<BillboardOwner> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public List<BillboardOwner> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private BillboardOwnerEntity toEntity(BillboardOwner domain) {
        BillboardOwnerEntity entity = new BillboardOwnerEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setCompanyName(domain.getOwnerDetails().getCompanyName());
        entity.setRegistrationNumber(domain.getOwnerDetails().getRegistrationNumber());
        entity.setContactEmail(domain.getOwnerDetails().getContactEmail());
        entity.setPhoneNumber(domain.getOwnerDetails().getPhoneNumber());
        entity.setRevenueShareRate(domain.getRevenueShareRate());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private BillboardOwner toDomain(BillboardOwnerEntity entity) {
        OwnerDetails details = new OwnerDetails(
                entity.getCompanyName(),
                entity.getRegistrationNumber(),
                entity.getContactEmail(),
                entity.getPhoneNumber()
        );

        return new BillboardOwner(
                entity.getId(),
                entity.getUserId(),
                details,
                entity.getRevenueShareRate(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
