package com.cscreativ.billboard.mediabuyer.infrastructure.persistence;

import com.cscreativ.billboard.mediabuyer.domain.MediaBuyer;
import com.cscreativ.billboard.mediabuyer.domain.repository.MediaBuyerRepository;
import com.cscreativ.billboard.mediabuyer.domain.valueobject.CompanyDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MediaBuyerPersistenceAdapter implements MediaBuyerRepository {

    private final MediaBuyerJpaRepository jpaRepository;

    public MediaBuyerPersistenceAdapter(MediaBuyerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MediaBuyer save(MediaBuyer mediaBuyer) {
        MediaBuyerEntity entity = toEntity(mediaBuyer);
        MediaBuyerEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<MediaBuyer> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<MediaBuyer> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public List<MediaBuyer> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private MediaBuyerEntity toEntity(MediaBuyer domain) {
        MediaBuyerEntity entity = new MediaBuyerEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setCompanyName(domain.getCompanyDetails().getCompanyName());
        entity.setTaxId(domain.getCompanyDetails().getTaxId());
        entity.setContactEmail(domain.getCompanyDetails().getContactEmail());
        entity.setPhoneNumber(domain.getCompanyDetails().getPhoneNumber());
        entity.setCreditLimit(domain.getCreditLimit());
        entity.setCurrentSpent(domain.getCurrentSpent());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private MediaBuyer toDomain(MediaBuyerEntity entity) {
        CompanyDetails companyDetails = new CompanyDetails(
                entity.getCompanyName(),
                entity.getTaxId(),
                entity.getContactEmail(),
                entity.getPhoneNumber()
        );

        return new MediaBuyer(
                entity.getId(),
                entity.getUserId(),
                companyDetails,
                entity.getCreditLimit(),
                entity.getCurrentSpent(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
