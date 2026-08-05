package com.cscreativ.billboard.storage.api;

import com.cscreativ.billboard.storage.api.mapper.StorageMapper;
import com.cscreativ.billboard.storage.api.response.StoredFileResponse;
import com.cscreativ.billboard.storage.application.StorageService;
import com.cscreativ.billboard.storage.domain.StoredFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/files")
public class StorageController {

    private final StorageService storageService;
    private final StorageMapper storageMapper;

    public StorageController(StorageService storageService, StorageMapper storageMapper) {
        this.storageService = storageService;
        this.storageMapper = storageMapper;
    }

    @PostMapping("/upload")
    public ResponseEntity<StoredFileResponse> uploadFile(@RequestParam("file") MultipartFile file,
                                                         @RequestParam("ownerId") UUID ownerId) throws IOException {
        StoredFile storedFile = storageService.storeFileForOwner(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes(),
                ownerId
        );
        String presignedUrl = storageService.getFilePresignedUrl(storedFile.getId());
        return ResponseEntity.ok(storageMapper.toResponse(storedFile, presignedUrl));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<StoredFileResponse>> getOwnerFiles(@PathVariable UUID ownerId) {
        List<StoredFileResponse> responses = storageService.getOwnerFiles(ownerId).stream()
                .map(file -> storageMapper.toResponse(file, storageService.getFilePresignedUrl(file.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable UUID id, @RequestParam("ownerId") UUID ownerId) {
        storageService.deleteOwnerFile(id, ownerId);
        return ResponseEntity.noContent().build();
    }
}
