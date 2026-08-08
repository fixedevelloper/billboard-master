package com.cscreativ.billboard.reporting.api;

import com.cscreativ.billboard.billboard.BillboardFacade;
import com.cscreativ.billboard.campaign.CampaignFacade;
import com.cscreativ.billboard.reporting.api.mapper.ReportingMapper;
import com.cscreativ.billboard.reporting.api.request.GenerateReportRequest;
import com.cscreativ.billboard.reporting.api.response.PerformanceReportResponse;
import com.cscreativ.billboard.reporting.application.ReportingService;
import com.cscreativ.billboard.reporting.domain.PerformanceReport;
import com.cscreativ.billboard.reporting.domain.ReportType;
import com.cscreativ.billboard.shared.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportingController {

    private final ReportingService reportingService;
    private final ReportingMapper reportingMapper;
    private final CampaignFacade campaignFacade;
    private final BillboardFacade billboardFacade;

    public ReportingController(ReportingService reportingService, ReportingMapper reportingMapper,
                                CampaignFacade campaignFacade, BillboardFacade billboardFacade) {
        this.reportingService = reportingService;
        this.reportingMapper = reportingMapper;
        this.campaignFacade = campaignFacade;
        this.billboardFacade = billboardFacade;
    }

    @PostMapping("/generate")
    public ResponseEntity<PerformanceReportResponse> generateReport(@RequestBody GenerateReportRequest request,
                                                                     @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requireOwnerOfTarget(request.type(), request.targetId(), currentUser);
        PerformanceReport report = reportingService.generateReport(
                request.targetId(),
                request.type(),
                request.startDate(),
                request.endDate(),
                request.totalImpressions(),
                request.totalInteractions(),
                request.totalRevenue(),
                request.occupancyRate()
        );
        return ResponseEntity.ok(reportingMapper.toResponse(report));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceReportResponse> getReportById(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        PerformanceReport report = reportingService.getReportById(id);
        requireOwnerOfTarget(report.getType(), report.getTargetId(), currentUser);
        return ResponseEntity.ok(reportingMapper.toResponse(report));
    }

    @GetMapping("/target/{targetId}")
    public ResponseEntity<List<PerformanceReportResponse>> getReportsByTarget(@PathVariable UUID targetId,
                                                                               @AuthenticationPrincipal AuthenticatedUser currentUser) {
        // Le type n'est pas connu à l'avance ici : on autorise si targetId correspond à une campagne de
        // l'appelant, à un panneau qu'il possède, ou à lui-même en tant que propriétaire - sinon admin.
        currentUser.require(currentUser.isAdmin()
                || currentUser.isAdvertiser(campaignFacade.findAdvertiserIdByCampaign(targetId).orElse(null))
                || currentUser.isOwner(billboardFacade.findOwnerIdByBillboard(targetId).orElse(null))
                || currentUser.isOwner(targetId));
        List<PerformanceReport> reports = reportingService.getReportsByTarget(targetId);
        return ResponseEntity.ok(reports.stream().map(reportingMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<PerformanceReportResponse>> getReportsByType(@PathVariable ReportType type,
                                                                             @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        List<PerformanceReport> reports = reportingService.getReportsByType(type);
        return ResponseEntity.ok(reports.stream().map(reportingMapper::toResponse).collect(Collectors.toList()));
    }

    /** targetId est polymorphe selon le type (campagne, panneau, propriétaire, ou aucun pour SYSTEM_ANALYTICS). */
    private void requireOwnerOfTarget(ReportType type, UUID targetId, AuthenticatedUser currentUser) {
        if (currentUser.isAdmin()) {
            return;
        }
        boolean authorized = switch (type) {
            case CAMPAIGN_PERFORMANCE -> currentUser.isAdvertiser(campaignFacade.findAdvertiserIdByCampaign(targetId).orElse(null));
            case BILLBOARD_OCCUPANCY -> currentUser.isOwner(billboardFacade.findOwnerIdByBillboard(targetId).orElse(null));
            case OWNER_REVENUE -> currentUser.isOwner(targetId);
            case SYSTEM_ANALYTICS -> false;
        };
        currentUser.require(authorized);
    }
}
