package com.cscreativ.billboard.campaign.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class MediaAsset {
    private final UUID fileId;
    private final String fileType;
    private final long fileSizeInBytes;

    public MediaAsset(UUID fileId, String fileType, long fileSizeInBytes) {
        if (fileId == null) {
            throw new IllegalArgumentException("Le fichier média est obligatoire");
        }
        if (fileType == null || fileType.isBlank()) {
            throw new IllegalArgumentException("Le type du fichier média est obligatoire");
        }
        if (fileSizeInBytes <= 0) {
            throw new IllegalArgumentException("La taille du fichier doit être supérieure à zéro");
        }
        this.fileId = fileId;
        this.fileType = fileType.trim().toLowerCase();
        this.fileSizeInBytes = fileSizeInBytes;
    }

    public UUID getFileId() { return fileId; }
    public String getFileType() { return fileType; }
    public long getFileSizeInBytes() { return fileSizeInBytes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaAsset that = (MediaAsset) o;
        return fileSizeInBytes == that.fileSizeInBytes &&
               Objects.equals(fileId, that.fileId) &&
               Objects.equals(fileType, that.fileType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId, fileType, fileSizeInBytes);
    }
}
