package com.cscreativ.billboard.billboard.infrastructure.persistence;

import com.cscreativ.billboard.billboard.domain.BillboardImage;
import com.cscreativ.billboard.billboard.domain.repository.BillboardImageRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BillboardImagePersistenceAdapter implements BillboardImageRepository {

    private final BillboardImageJpaRepository jpaRepository;

    public BillboardImagePersistenceAdapter(BillboardImageJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public BillboardImage save(BillboardImage image) {
        BillboardImageEntity entity = toEntity(image);
        BillboardImageEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<BillboardImage> findByBillboardId(UUID billboardId) {
        return jpaRepository.findByBillboardId(billboardId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private BillboardImageEntity toEntity(BillboardImage domain) {
        BillboardImageEntity entity = new BillboardImageEntity();
        entity.setId(domain.getId());
        entity.setBillboardId(domain.getBillboardId());
        entity.setFileId(domain.getFileId());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private BillboardImage toDomain(BillboardImageEntity entity) {
        return new BillboardImage(entity.getId(), entity.getBillboardId(), entity.getFileId(), entity.getCreatedAt());
    }
}
