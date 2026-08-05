package com.cscreativ.billboard.storage.domain.service;

public interface PhysicalStorageService {
    String store(byte[] content, String filename);
    byte[] retrieve(String storagePath);
    void delete(String storagePath);
    String generatePublicUrl(String storagePath);
}
