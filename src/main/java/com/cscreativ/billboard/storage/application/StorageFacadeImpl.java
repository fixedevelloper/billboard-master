package com.cscreativ.billboard.storage.application;

import com.cscreativ.billboard.storage.StorageFacade;
import com.cscreativ.billboard.storage.domain.StoredFile;
import com.cscreativ.billboard.storage.domain.repository.StoredFileRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class StorageFacadeImpl implements StorageFacade {

    private final StorageService storageService;
    private final StoredFileRepository storedFileRepository;

    public StorageFacadeImpl(StorageService storageService, StoredFileRepository storedFileRepository) {
        this.storageService = storageService;
        this.storedFileRepository = storedFileRepository;
    }

    @Override
    public StoredFile uploadFileForOwner(String filename, String contentType, byte[] content, UUID ownerId) {
        return storageService.storeFileForOwner(filename, contentType, content, ownerId);
    }

    @Override
    public Optional<StoredFile> getFileMetadata(UUID fileId) {
        return storedFileRepository.findById(fileId);
    }

    @Override
    public List<StoredFile> getFilesByOwner(UUID ownerId) {
        return storageService.getOwnerFiles(ownerId);
    }

    @Override
    public String getPresignedUrl(UUID fileId) {
        return storageService.getFilePresignedUrl(fileId);
    }

    @Override
    public void deleteFile(UUID fileId, UUID ownerId) {
        storageService.deleteOwnerFile(fileId, ownerId);
    }
}
