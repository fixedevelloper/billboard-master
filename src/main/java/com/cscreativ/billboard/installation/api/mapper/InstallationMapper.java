package com.cscreativ.billboard.installation.api.mapper;

import com.cscreativ.billboard.installation.api.response.InstallationTaskResponse;
import com.cscreativ.billboard.installation.domain.InstallationTask;
import org.springframework.stereotype.Component;

@Component
public class InstallationMapper {

    public InstallationTaskResponse toResponse(InstallationTask task, String proofPhotoUrl) {
        return new InstallationTaskResponse(
                task.getId(),
                task.getCampaignId(),
                task.getBillboardId(),
                task.getTechnicianId(),
                task.getScheduledDate(),
                task.getStatus().name(),
                proofPhotoUrl,
                task.getProof() != null ? task.getProof().getNotes() : null,
                task.getCreatedAt()
        );
    }
}
