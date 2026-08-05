package com.cscreativ.billboard.advertiser.infrastructure.persistence;

import com.cscreativ.billboard.advertiser.domain.Advertiser;
import com.cscreativ.billboard.advertiser.domain.repository.AdvertiserRepository;
import com.cscreativ.billboard.advertiser.domain.valueobject.CompanyName;
import com.cscreativ.billboard.advertiser.domain.valueobject.TaxNumber;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AdvertiserPersistenceAdapter implements AdvertiserRepository {

    private final AdvertiserJpaRepository jpaRepository;

    public AdvertiserPersistenceAdapter(AdvertiserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Advertiser save(Advertiser advertiser) {
        AdvertiserEntity entity = toEntity(advertiser);
        AdvertiserEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Advertiser> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Advertiser> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public boolean existsByTaxNumber(TaxNumber taxNumber) {
        return jpaRepository.existsByTaxNumber(taxNumber.getValue());
    }

    private AdvertiserEntity toEntity(Advertiser domain) {
        AdvertiserEntity entity = new AdvertiserEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setCompanyName(domain.getCompanyName().getValue());
        entity.setTaxNumber(domain.getTaxNumber().getValue());
        entity.setContactEmail(domain.getContactEmail());
        entity.setContactPhone(domain.getContactPhone());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private Advertiser toDomain(AdvertiserEntity entity) {
        return new Advertiser(
                entity.getId(),
                entity.getUserId(),
                new CompanyName(entity.getCompanyName()),
                new TaxNumber(entity.getTaxNumber()),
                entity.getContactEmail(),
                entity.getContactPhone(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
