package com.cscreativ.billboard.installation.domain.valueobject;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class InstallationProof {
    private final UUID photoFileId;
    private final String notes;
    private final LocalDateTime uploadedAt;

    public InstallationProof(UUID photoFileId, String notes, LocalDateTime uploadedAt) {
        this.photoFileId = photoFileId;
        this.notes = notes != null ? notes.trim() : "";
        this.uploadedAt = uploadedAt != null ? uploadedAt : LocalDateTime.now();
    }

    public UUID getPhotoFileId() { return photoFileId; }
    public String getNotes() { return notes; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstallationProof proof = (InstallationProof) o;
        return Objects.equals(photoFileId, proof.photoFileId) &&
               Objects.equals(notes, proof.notes) &&
               Objects.equals(uploadedAt, proof.uploadedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoFileId, notes, uploadedAt);
    }
}
