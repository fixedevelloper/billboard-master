package com.cscreativ.billboard.storage.domain.repository;

import com.cscreativ.billboard.storage.domain.StoredFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoredFileRepository {
    StoredFile save(StoredFile storedFile);
    Optional<StoredFile> findById(UUID id);
    List<StoredFile> findByOwnerId(UUID ownerId);
    void deleteById(UUID id);
}
