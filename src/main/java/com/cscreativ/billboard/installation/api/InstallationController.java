package com.cscreativ.billboard.installation.api;

import com.cscreativ.billboard.billboard.BillboardFacade;
import com.cscreativ.billboard.booking.BookingFacade;
import com.cscreativ.billboard.campaign.CampaignFacade;
import com.cscreativ.billboard.installation.api.mapper.InstallationMapper;
import com.cscreativ.billboard.installation.api.request.CompleteTaskRequest;
import com.cscreativ.billboard.installation.api.request.ScheduleTaskRequest;
import com.cscreativ.billboard.installation.api.response.InstallationTaskResponse;
import com.cscreativ.billboard.installation.application.InstallationService;
import com.cscreativ.billboard.installation.domain.InstallationTask;
import com.cscreativ.billboard.shared.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/installations")
public class InstallationController {

    private final InstallationService installationService;
    private final InstallationMapper installationMapper;
    private final CampaignFacade campaignFacade;
    private final BookingFacade bookingFacade;
    private final BillboardFacade billboardFacade;

    public InstallationController(InstallationService installationService, InstallationMapper installationMapper,
                                   CampaignFacade campaignFacade, BookingFacade bookingFacade, BillboardFacade billboardFacade) {
        this.installationService = installationService;
        this.installationMapper = installationMapper;
        this.campaignFacade = campaignFacade;
        this.bookingFacade = bookingFacade;
        this.billboardFacade = billboardFacade;
    }

    @PostMapping
    public ResponseEntity<InstallationTaskResponse> scheduleTask(@RequestBody ScheduleTaskRequest request,
                                                                  @AuthenticationPrincipal AuthenticatedUser currentUser) {
        UUID advertiserId = campaignFacade.findAdvertiserIdByCampaign(request.campaignId()).orElse(null);
        UUID ownerId = billboardFacade.findOwnerIdByBillboard(request.billboardId()).orElse(null);
        currentUser.require(currentUser.isAdmin() || currentUser.isAdvertiser(advertiserId) || currentUser.isOwner(ownerId));
        InstallationTask task = installationService.scheduleTask(
                request.campaignId(),
                request.billboardId(),
                request.technicianId(),
                request.scheduledDate()
        );
        return ResponseEntity.ok(installationMapper.toResponse(task));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Void> startTask(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requirePartyToTask(installationService.getTaskById(id), currentUser);
        installationService.startTask(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable UUID id, @RequestBody CompleteTaskRequest request,
                                              @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requirePartyToTask(installationService.getTaskById(id), currentUser);
        installationService.completeTask(id, request.photoUrl(), request.notes());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstallationTaskResponse> getTaskById(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        InstallationTask task = installationService.getTaskById(id);
        requirePartyToTask(task, currentUser);
        return ResponseEntity.ok(installationMapper.toResponse(task));
    }

    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<List<InstallationTaskResponse>> getTasksByCampaign(@PathVariable UUID campaignId,
                                                                              @AuthenticationPrincipal AuthenticatedUser currentUser) {
        UUID advertiserId = campaignFacade.findAdvertiserIdByCampaign(campaignId).orElse(null);
        currentUser.require(currentUser.isAdmin() || currentUser.isAdvertiser(advertiserId) || currentUser.isOwner(resolveOwnerIdForCampaign(campaignId)));
        List<InstallationTask> tasks = installationService.getTasksByCampaign(campaignId);
        return ResponseEntity.ok(tasks.stream().map(installationMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<InstallationTaskResponse>> getTasksByTechnician(@PathVariable UUID technicianId,
                                                                                @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        List<InstallationTask> tasks = installationService.getTasksByTechnician(technicianId);
        return ResponseEntity.ok(tasks.stream().map(installationMapper::toResponse).collect(Collectors.toList()));
    }

    private void requirePartyToTask(InstallationTask task, AuthenticatedUser currentUser) {
        UUID advertiserId = campaignFacade.findAdvertiserIdByCampaign(task.getCampaignId()).orElse(null);
        UUID ownerId = billboardFacade.findOwnerIdByBillboard(task.getBillboardId()).orElse(null);
        currentUser.require(currentUser.isAdmin() || currentUser.isAdvertiser(advertiserId) || currentUser.isOwner(ownerId));
    }

    private UUID resolveOwnerIdForCampaign(UUID campaignId) {
        UUID bookingId = campaignFacade.findBookingIdByCampaign(campaignId).orElse(null);
        if (bookingId == null) {
            return null;
        }
        UUID billboardId = bookingFacade.findBillboardIdByBooking(bookingId).orElse(null);
        return billboardId == null ? null : billboardFacade.findOwnerIdByBillboard(billboardId).orElse(null);
    }
}
