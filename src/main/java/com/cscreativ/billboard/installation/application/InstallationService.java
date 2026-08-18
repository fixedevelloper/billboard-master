package com.cscreativ.billboard.installation.application;

import com.cscreativ.billboard.campaign.CampaignFacade;
import com.cscreativ.billboard.installation.domain.InstallationTask;
import com.cscreativ.billboard.installation.domain.exception.InstallationTaskNotFoundException;
import com.cscreativ.billboard.installation.domain.repository.InstallationTaskRepository;
import com.cscreativ.billboard.installation.events.InstallationCompletedEvent;
import com.cscreativ.billboard.installation.events.InstallationScheduledEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InstallationService {

    private final InstallationTaskRepository taskRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CampaignFacade campaignFacade;

    public InstallationService(InstallationTaskRepository taskRepository, ApplicationEventPublisher eventPublisher,
                                CampaignFacade campaignFacade) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
        this.campaignFacade = campaignFacade;
    }

    @Transactional
    public InstallationTask scheduleTask(UUID campaignId, UUID billboardId, UUID technicianId, LocalDateTime scheduledDate) {
        InstallationTask task = InstallationTask.schedule(campaignId, billboardId, technicianId, scheduledDate);
        InstallationTask saved = taskRepository.save(task);

        eventPublisher.publishEvent(new InstallationScheduledEvent(saved.getId(), saved.getCampaignId(), saved.getBillboardId(), saved.getScheduledDate(), LocalDateTime.now()));
        return saved;
    }

    @Transactional
    public void startTask(UUID taskId) {
        InstallationTask task = getTaskById(taskId);
        task.startInstallation();
        taskRepository.save(task);
    }

    @Transactional
    public void completeTask(UUID taskId, UUID photoFileId, String notes) {
        InstallationTask task = getTaskById(taskId);
        task.completeInstallation(photoFileId, notes);
        InstallationTask saved = taskRepository.save(task);

        eventPublisher.publishEvent(new InstallationCompletedEvent(saved.getId(), saved.getCampaignId(), saved.getBillboardId(), photoFileId, LocalDateTime.now()));

        // La pose physique du visuel est le signal métier qui fait passer la campagne à ACTIVE.
        campaignFacade.activateCampaign(saved.getCampaignId());
    }

    public InstallationTask getTaskById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new InstallationTaskNotFoundException("Tâche d'installation non trouvée avec l'id : " + id));
    }

    public List<InstallationTask> getTasksByCampaign(UUID campaignId) {
        return taskRepository.findByCampaignId(campaignId);
    }

    public List<InstallationTask> getTasksByTechnician(UUID technicianId) {
        return taskRepository.findByTechnicianId(technicianId);
    }
}
