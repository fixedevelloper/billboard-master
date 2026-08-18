package com.cscreativ.billboard.installation.infrastructure.persistence;

import com.cscreativ.billboard.installation.domain.InstallationTask;
import com.cscreativ.billboard.installation.domain.repository.InstallationTaskRepository;
import com.cscreativ.billboard.installation.domain.valueobject.InstallationProof;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class InstallationTaskPersistenceAdapter implements InstallationTaskRepository {

    private final InstallationTaskJpaRepository jpaRepository;

    public InstallationTaskPersistenceAdapter(InstallationTaskJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public InstallationTask save(InstallationTask task) {
        InstallationTaskEntity entity = toEntity(task);
        InstallationTaskEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<InstallationTask> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<InstallationTask> findByCampaignId(UUID campaignId) {
        return jpaRepository.findByCampaignId(campaignId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<InstallationTask> findByTechnicianId(UUID technicianId) {
        return jpaRepository.findByTechnicianId(technicianId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private InstallationTaskEntity toEntity(InstallationTask domain) {
        InstallationTaskEntity entity = new InstallationTaskEntity();
        entity.setId(domain.getId());
        entity.setCampaignId(domain.getCampaignId());
        entity.setBillboardId(domain.getBillboardId());
        entity.setTechnicianId(domain.getTechnicianId());
        entity.setScheduledDate(domain.getScheduledDate());
        entity.setStatus(domain.getStatus());

        if (domain.getProof() != null) {
            entity.setProofFileId(domain.getProof().getPhotoFileId());
            entity.setProofNotes(domain.getProof().getNotes());
            entity.setProofUploadedAt(domain.getProof().getUploadedAt());
        }

        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private InstallationTask toDomain(InstallationTaskEntity entity) {
        InstallationProof proof = entity.getProofUploadedAt() != null ?
                new InstallationProof(entity.getProofFileId(), entity.getProofNotes(), entity.getProofUploadedAt()) : null;

        return new InstallationTask(
                entity.getId(),
                entity.getCampaignId(),
                entity.getBillboardId(),
                entity.getTechnicianId(),
                entity.getScheduledDate(),
                entity.getStatus(),
                proof,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
