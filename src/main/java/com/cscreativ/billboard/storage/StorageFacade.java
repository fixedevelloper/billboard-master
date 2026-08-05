package com.cscreativ.billboard.storage;

import com.cscreativ.billboard.storage.domain.StoredFile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StorageFacade {
    StoredFile uploadFileForOwner(String filename, String contentType, byte[] content, UUID ownerId);
    Optional<StoredFile> getFileMetadata(UUID fileId);
    List<StoredFile> getFilesByOwner(UUID ownerId);
    String getPresignedUrl(UUID fileId);
    void deleteFile(UUID fileId, UUID ownerId);
}
