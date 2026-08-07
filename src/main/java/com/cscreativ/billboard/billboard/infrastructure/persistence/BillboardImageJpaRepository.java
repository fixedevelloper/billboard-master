package com.cscreativ.billboard.billboard.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BillboardImageJpaRepository extends JpaRepository<BillboardImageEntity, UUID> {
    List<BillboardImageEntity> findByBillboardId(UUID billboardId);
}
