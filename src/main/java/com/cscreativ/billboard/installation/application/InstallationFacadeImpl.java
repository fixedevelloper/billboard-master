package com.cscreativ.billboard.installation.application;

import com.cscreativ.billboard.installation.InstallationFacade;
import com.cscreativ.billboard.installation.domain.InstallationTask;
import com.cscreativ.billboard.installation.domain.TaskStatus;
import com.cscreativ.billboard.installation.domain.repository.InstallationTaskRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class InstallationFacadeImpl implements InstallationFacade {

    private final InstallationTaskRepository taskRepository;

    public InstallationFacadeImpl(InstallationTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Optional<InstallationTask> findTaskById(UUID taskId) {
        return taskRepository.findById(taskId);
    }

    @Override
    public boolean isCampaignInstalled(UUID campaignId) {
        List<InstallationTask> tasks = taskRepository.findByCampaignId(campaignId);
        return !tasks.isEmpty() && tasks.stream().allMatch(t -> t.getStatus() == TaskStatus.COMPLETED);
    }
}
