package com.cscreativ.billboard.storage.domain;

import com.cscreativ.billboard.storage.domain.valueobject.FileMetadata;

import java.time.LocalDateTime;
import java.util.UUID;

public class StoredFile {
    private final UUID id;
    private final FileMetadata metadata;
    private final String storagePath;
    private final StorageProvider provider;
    private final UUID ownerId;
    private final LocalDateTime uploadedAt;

    public StoredFile(UUID id, FileMetadata metadata, String storagePath, StorageProvider provider, UUID ownerId, LocalDateTime uploadedAt) {
        if (ownerId == null) {
            throw new IllegalArgumentException("Chaque fichier doit être rattaché à un propriétaire (ownerId)");
        }
        this.id = id;
        this.metadata = metadata;
        this.storagePath = storagePath;
        this.provider = provider;
        this.ownerId = ownerId;
        this.uploadedAt = uploadedAt;
    }

    public static StoredFile createForOwner(FileMetadata metadata, String storagePath, UUID ownerId) {
        return new StoredFile(UUID.randomUUID(), metadata, storagePath, StorageProvider.MINIO, ownerId, LocalDateTime.now());
    }

    public UUID getId() { return id; }
    public FileMetadata getMetadata() { return metadata; }
    public String getStoragePath() { return storagePath; }
    public StorageProvider getProvider() { return provider; }
    public UUID getOwnerId() { return ownerId; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
}
