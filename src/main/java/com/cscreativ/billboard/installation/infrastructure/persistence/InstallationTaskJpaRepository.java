package com.cscreativ.billboard.installation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InstallationTaskJpaRepository extends JpaRepository<InstallationTaskEntity, UUID> {
    List<InstallationTaskEntity> findByCampaignId(UUID campaignId);
    List<InstallationTaskEntity> findByTechnicianId(UUID technicianId);
}
