package com.cscreativ.billboard.billboard.infrastructure.persistence;

import com.cscreativ.billboard.billboard.domain.Billboard;
import com.cscreativ.billboard.billboard.domain.BillboardStatus;
import com.cscreativ.billboard.billboard.domain.repository.BillboardRepository;
import com.cscreativ.billboard.billboard.domain.valueobject.Dimensions;
import com.cscreativ.billboard.billboard.domain.valueobject.Location;
import com.cscreativ.billboard.billboard.domain.valueobject.Pricing;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BillboardPersistenceAdapter implements BillboardRepository {

    private final BillboardJpaRepository jpaRepository;

    public BillboardPersistenceAdapter(BillboardJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Billboard save(Billboard billboard) {
        BillboardEntity entity = toEntity(billboard);
        BillboardEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Billboard> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Billboard> findAllByCity(String city) {
        return jpaRepository.findByCity(city).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Billboard> findAllByStatus(BillboardStatus status) {
        return jpaRepository.findByStatus(status).stream().map(this::toDomain).collect(Collectors.toList());
    }

    private BillboardEntity toEntity(Billboard domain) {
        BillboardEntity entity = new BillboardEntity();
        entity.setId(domain.getId());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setType(domain.getType());
        entity.setStatus(domain.getStatus());
        entity.setAddress(domain.getLocation().getAddress());
        entity.setCity(domain.getLocation().getCity());
        entity.setLatitude(domain.getLocation().getLatitude());
        entity.setLongitude(domain.getLocation().getLongitude());
        entity.setWidth(domain.getDimensions().getWidth());
        entity.setHeight(domain.getDimensions().getHeight());
        entity.setDailyRate(domain.getPricing().getDailyRate());
        entity.setCurrency(domain.getPricing().getCurrency());
        entity.setOwnerId(domain.getOwnerId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private Billboard toDomain(BillboardEntity entity) {
        return new Billboard(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getType(),
                entity.getStatus(),
                new Location(entity.getAddress(), entity.getCity(), entity.getLatitude(), entity.getLongitude()),
                new Dimensions(entity.getWidth(), entity.getHeight()),
                new Pricing(entity.getDailyRate(), entity.getCurrency()),
                entity.getOwnerId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
