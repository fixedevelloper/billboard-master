package com.cscreativ.billboard.creative.infrastructure.persistence;

import com.cscreativ.billboard.creative.domain.CreativeProof;
import com.cscreativ.billboard.creative.domain.repository.CreativeProofRepository;
import com.cscreativ.billboard.creative.domain.valueobject.ProofDimensions;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CreativeProofPersistenceAdapter implements CreativeProofRepository {

    private final CreativeProofJpaRepository jpaRepository;

    public CreativeProofPersistenceAdapter(CreativeProofJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CreativeProof save(CreativeProof proof) {
        CreativeProofEntity entity = toEntity(proof);
        CreativeProofEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<CreativeProof> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<CreativeProof> findByCampaignId(UUID campaignId) {
        return jpaRepository.findByCampaignIdOrderByVersionDesc(campaignId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CreativeProof> findLatestByCampaignId(UUID campaignId) {
        return jpaRepository.findFirstByCampaignIdOrderByVersionDesc(campaignId).map(this::toDomain);
    }

    private CreativeProofEntity toEntity(CreativeProof domain) {
        CreativeProofEntity entity = new CreativeProofEntity();
        entity.setId(domain.getId());
        entity.setCampaignId(domain.getCampaignId());
        entity.setVersion(domain.getVersion());
        entity.setFileUrl(domain.getFileUrl());
        entity.setWidthInPixels(domain.getDimensions().getWidthInPixels());
        entity.setHeightInPixels(domain.getDimensions().getHeightInPixels());
        entity.setStatus(domain.getStatus());
        entity.setFeedback(domain.getFeedback());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private CreativeProof toDomain(CreativeProofEntity entity) {
        return new CreativeProof(
                entity.getId(),
                entity.getCampaignId(),
                entity.getVersion(),
                entity.getFileUrl(),
                new ProofDimensions(entity.getWidthInPixels(), entity.getHeightInPixels()),
                entity.getStatus(),
                entity.getFeedback(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
