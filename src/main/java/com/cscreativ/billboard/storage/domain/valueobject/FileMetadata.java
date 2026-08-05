package com.cscreativ.billboard.storage.domain.valueobject;

import java.util.Objects;

public class FileMetadata {
    private final String originalFilename;
    private final String contentType;
    private final long size;

    public FileMetadata(String originalFilename, String contentType, long size) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Le nom du fichier ne peut pas être vide");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("La taille du fichier doit être supérieure à 0");
        }
        this.originalFilename = originalFilename;
        this.contentType = contentType != null ? contentType : "application/octet-stream";
        this.size = size;
    }

    public String getOriginalFilename() { return originalFilename; }
    public String getContentType() { return contentType; }
    public long getSize() { return size; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileMetadata metadata = (FileMetadata) o;
        return size == metadata.size && Objects.equals(originalFilename, metadata.originalFilename) && Objects.equals(contentType, metadata.contentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalFilename, contentType, size);
    }
}
