package com.cscreativ.billboard.storage.infrastructure.persistence;

import com.cscreativ.billboard.storage.domain.StoredFile;
import com.cscreativ.billboard.storage.domain.repository.StoredFileRepository;
import com.cscreativ.billboard.storage.domain.valueobject.FileMetadata;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class StoredFilePersistenceAdapter implements StoredFileRepository {

    private final StoredFileJpaRepository jpaRepository;

    public StoredFilePersistenceAdapter(StoredFileJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public StoredFile save(StoredFile storedFile) {
        StoredFileEntity entity = toEntity(storedFile);
        StoredFileEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<StoredFile> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<StoredFile> findByOwnerId(UUID ownerId) {
        return jpaRepository.findByOwnerId(ownerId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private StoredFileEntity toEntity(StoredFile domain) {
        StoredFileEntity entity = new StoredFileEntity();
        entity.setId(domain.getId());
        entity.setOriginalFilename(domain.getMetadata().getOriginalFilename());
        entity.setContentType(domain.getMetadata().getContentType());
        entity.setSize(domain.getMetadata().getSize());
        entity.setStoragePath(domain.getStoragePath());
        entity.setProvider(domain.getProvider());
        entity.setOwnerId(domain.getOwnerId());
        entity.setUploadedAt(domain.getUploadedAt());
        return entity;
    }

    private StoredFile toDomain(StoredFileEntity entity) {
        FileMetadata metadata = new FileMetadata(entity.getOriginalFilename(), entity.getContentType(), entity.getSize());
        return new StoredFile(
                entity.getId(),
                metadata,
                entity.getStoragePath(),
                entity.getProvider(),
                entity.getOwnerId(),
                entity.getUploadedAt()
        );
    }
}
