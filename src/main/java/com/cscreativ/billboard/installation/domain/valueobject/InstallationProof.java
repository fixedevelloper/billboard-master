package com.cscreativ.billboard.installation.domain.valueobject;

import java.time.LocalDateTime;
import java.util.Objects;

public class InstallationProof {
    private final String photoUrl;
    private final String notes;
    private final LocalDateTime uploadedAt;

    public InstallationProof(String photoUrl, String notes, LocalDateTime uploadedAt) {
        if (photoUrl == null || photoUrl.isBlank()) {
            throw new IllegalArgumentException("L'URL de la photo de preuve est obligatoire");
        }
        this.photoUrl = photoUrl.trim();
        this.notes = notes != null ? notes.trim() : "";
        this.uploadedAt = uploadedAt != null ? uploadedAt : LocalDateTime.now();
    }

    public String getPhotoUrl() { return photoUrl; }
    public String getNotes() { return notes; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstallationProof proof = (InstallationProof) o;
        return Objects.equals(photoUrl, proof.photoUrl) &&
               Objects.equals(notes, proof.notes) &&
               Objects.equals(uploadedAt, proof.uploadedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoUrl, notes, uploadedAt);
    }
}
