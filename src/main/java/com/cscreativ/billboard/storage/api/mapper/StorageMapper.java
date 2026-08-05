package com.cscreativ.billboard.storage.api.mapper;

import com.cscreativ.billboard.storage.api.response.StoredFileResponse;
import com.cscreativ.billboard.storage.domain.StoredFile;
import org.springframework.stereotype.Component;

@Component
public class StorageMapper {

    public StoredFileResponse toResponse(StoredFile storedFile, String publicUrl) {
        return new StoredFileResponse(
                storedFile.getId(),
                storedFile.getMetadata().getOriginalFilename(),
                storedFile.getMetadata().getContentType(),
                storedFile.getMetadata().getSize(),
                publicUrl,
                storedFile.getOwnerId(),
                storedFile.getUploadedAt()
        );
    }
}
