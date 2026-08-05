package com.cscreativ.billboard.installation.api;

import com.cscreativ.billboard.installation.api.mapper.InstallationMapper;
import com.cscreativ.billboard.installation.api.request.CompleteTaskRequest;
import com.cscreativ.billboard.installation.api.request.ScheduleTaskRequest;
import com.cscreativ.billboard.installation.api.response.InstallationTaskResponse;
import com.cscreativ.billboard.installation.application.InstallationService;
import com.cscreativ.billboard.installation.domain.InstallationTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/installations")
public class InstallationController {

    private final InstallationService installationService;
    private final InstallationMapper installationMapper;

    public InstallationController(InstallationService installationService, InstallationMapper installationMapper) {
        this.installationService = installationService;
        this.installationMapper = installationMapper;
    }

    @PostMapping
    public ResponseEntity<InstallationTaskResponse> scheduleTask(@RequestBody ScheduleTaskRequest request) {
        InstallationTask task = installationService.scheduleTask(
                request.campaignId(),
                request.billboardId(),
                request.technicianId(),
                request.scheduledDate()
        );
        return ResponseEntity.ok(installationMapper.toResponse(task));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Void> startTask(@PathVariable UUID id) {
        installationService.startTask(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable UUID id, @RequestBody CompleteTaskRequest request) {
        installationService.completeTask(id, request.photoUrl(), request.notes());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstallationTaskResponse> getTaskById(@PathVariable UUID id) {
        InstallationTask task = installationService.getTaskById(id);
        return ResponseEntity.ok(installationMapper.toResponse(task));
    }

    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<List<InstallationTaskResponse>> getTasksByCampaign(@PathVariable UUID campaignId) {
        List<InstallationTask> tasks = installationService.getTasksByCampaign(campaignId);
        return ResponseEntity.ok(tasks.stream().map(installationMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<InstallationTaskResponse>> getTasksByTechnician(@PathVariable UUID technicianId) {
        List<InstallationTask> tasks = installationService.getTasksByTechnician(technicianId);
        return ResponseEntity.ok(tasks.stream().map(installationMapper::toResponse).collect(Collectors.toList()));
    }
}
