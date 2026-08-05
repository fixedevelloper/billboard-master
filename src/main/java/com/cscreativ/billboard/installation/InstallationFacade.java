package com.cscreativ.billboard.installation;

import com.cscreativ.billboard.installation.domain.InstallationTask;

import java.util.Optional;
import java.util.UUID;

public interface InstallationFacade {
    Optional<InstallationTask> findTaskById(UUID taskId);
    boolean isCampaignInstalled(UUID campaignId);
}
