package com.cscreativ.billboard.storage.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StoredFileJpaRepository extends JpaRepository<StoredFileEntity, UUID> {
    List<StoredFileEntity> findByOwnerId(UUID ownerId);
}
