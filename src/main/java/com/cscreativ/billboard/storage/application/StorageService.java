package com.cscreativ.billboard.storage.application;

import com.cscreativ.billboard.storage.domain.StoredFile;
import com.cscreativ.billboard.storage.domain.exception.FileNotFoundException;
import com.cscreativ.billboard.storage.domain.repository.StoredFileRepository;
import com.cscreativ.billboard.storage.domain.service.PhysicalStorageService;
import com.cscreativ.billboard.storage.domain.valueobject.FileMetadata;
import com.cscreativ.billboard.storage.events.FileDeletedEvent;
import com.cscreativ.billboard.storage.events.FileUploadedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StorageService {

    private final StoredFileRepository storedFileRepository;
    private final PhysicalStorageService physicalStorageService;
    private final ApplicationEventPublisher eventPublisher;

    public StorageService(StoredFileRepository storedFileRepository, 
                          PhysicalStorageService physicalStorageService, 
                          ApplicationEventPublisher eventPublisher) {
        this.storedFileRepository = storedFileRepository;
        this.physicalStorageService = physicalStorageService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public StoredFile storeFileForOwner(String originalFilename, String contentType, byte[] content, UUID ownerId) {
        FileMetadata metadata = new FileMetadata(originalFilename, contentType, content.length);
        String storagePath = physicalStorageService.store(content, originalFilename);

        StoredFile storedFile = StoredFile.createForOwner(metadata, storagePath, ownerId);
        StoredFile saved = storedFileRepository.save(storedFile);

        eventPublisher.publishEvent(new FileUploadedEvent(saved.getId(), originalFilename, ownerId, LocalDateTime.now()));
        return saved;
    }

    public List<StoredFile> getOwnerFiles(UUID ownerId) {
        return storedFileRepository.findByOwnerId(ownerId);
    }

    public String getFilePresignedUrl(UUID fileId) {
        StoredFile file = storedFileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("Fichier non trouvé : " + fileId));
        return physicalStorageService.generatePublicUrl(file.getStoragePath());
    }

    @Transactional
    public void deleteOwnerFile(UUID fileId, UUID ownerId) {
        StoredFile file = storedFileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("Fichier non trouvé : " + fileId));

        if (!file.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Action non autorisée : cet utilisateur n'est pas propriétaire du fichier.");
        }

        physicalStorageService.delete(file.getStoragePath());
        storedFileRepository.deleteById(fileId);

        eventPublisher.publishEvent(new FileDeletedEvent(fileId, ownerId, LocalDateTime.now()));
    }
}
