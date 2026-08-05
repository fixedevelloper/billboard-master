package com.cscreativ.billboard.campaign.domain.valueobject;

import java.util.Objects;

public class MediaAsset {
    private final String url;
    private final String fileType;
    private final long fileSizeInBytes;

    public MediaAsset(String url, String fileType, long fileSizeInBytes) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("L'URL du fichier média est obligatoire");
        }
        if (fileType == null || fileType.isBlank()) {
            throw new IllegalArgumentException("Le type du fichier média est obligatoire");
        }
        if (fileSizeInBytes <= 0) {
            throw new IllegalArgumentException("La taille du fichier doit être supérieure à zéro");
        }
        this.url = url.trim();
        this.fileType = fileType.trim().toLowerCase();
        this.fileSizeInBytes = fileSizeInBytes;
    }

    public String getUrl() { return url; }
    public String getFileType() { return fileType; }
    public long getFileSizeInBytes() { return fileSizeInBytes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaAsset that = (MediaAsset) o;
        return fileSizeInBytes == that.fileSizeInBytes &&
               Objects.equals(url, that.url) &&
               Objects.equals(fileType, that.fileType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, fileType, fileSizeInBytes);
    }
}
