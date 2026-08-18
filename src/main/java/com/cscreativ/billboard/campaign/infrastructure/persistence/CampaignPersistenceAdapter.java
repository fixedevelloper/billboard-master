package com.cscreativ.billboard.campaign.infrastructure.persistence;

import com.cscreativ.billboard.campaign.domain.Campaign;
import com.cscreativ.billboard.campaign.domain.CampaignStatus;
import com.cscreativ.billboard.campaign.domain.repository.CampaignRepository;
import com.cscreativ.billboard.campaign.domain.valueobject.MediaAsset;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CampaignPersistenceAdapter implements CampaignRepository {

    private final CampaignJpaRepository jpaRepository;

    public CampaignPersistenceAdapter(CampaignJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Campaign save(Campaign campaign) {
        CampaignEntity entity = toEntity(campaign);
        CampaignEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Campaign> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Campaign> findByAdvertiserId(UUID advertiserId) {
        return jpaRepository.findByAdvertiserId(advertiserId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Campaign> findByStatus(CampaignStatus status) {
        return jpaRepository.findByStatus(status).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Campaign> findByBookingId(UUID bookingId) {
        return jpaRepository.findByBookingId(bookingId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Campaign> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private CampaignEntity toEntity(Campaign domain) {
        CampaignEntity entity = new CampaignEntity();
        entity.setId(domain.getId());
        entity.setBookingId(domain.getBookingId());
        entity.setAdvertiserId(domain.getAdvertiserId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        MediaAsset mediaAsset = domain.getMediaAsset();
        if (mediaAsset != null) {
            entity.setMediaFileId(mediaAsset.getFileId());
            entity.setMediaFileType(mediaAsset.getFileType());
            entity.setMediaFileSizeInBytes(mediaAsset.getFileSizeInBytes());
        }
        entity.setStatus(domain.getStatus());
        entity.setRejectionReason(domain.getRejectionReason());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private Campaign toDomain(CampaignEntity entity) {
        MediaAsset mediaAsset = entity.getMediaFileId() == null
                ? null
                : new MediaAsset(entity.getMediaFileId(), entity.getMediaFileType(), entity.getMediaFileSizeInBytes());
        return new Campaign(
                entity.getId(),
                entity.getBookingId(),
                entity.getAdvertiserId(),
                entity.getName(),
                entity.getDescription(),
                mediaAsset,
                entity.getStatus(),
                entity.getRejectionReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
