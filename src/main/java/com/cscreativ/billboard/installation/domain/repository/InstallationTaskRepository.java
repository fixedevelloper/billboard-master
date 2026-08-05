package com.cscreativ.billboard.installation.domain.repository;

import com.cscreativ.billboard.installation.domain.InstallationTask;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InstallationTaskRepository {
    InstallationTask save(InstallationTask task);
    Optional<InstallationTask> findById(UUID id);
    List<InstallationTask> findByCampaignId(UUID campaignId);
    List<InstallationTask> findByTechnicianId(UUID technicianId);
}
