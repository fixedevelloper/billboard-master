package com.cscreativ.billboard.reporting.api;

import com.cscreativ.billboard.reporting.api.mapper.ReportingMapper;
import com.cscreativ.billboard.reporting.api.request.GenerateReportRequest;
import com.cscreativ.billboard.reporting.api.response.PerformanceReportResponse;
import com.cscreativ.billboard.reporting.application.ReportingService;
import com.cscreativ.billboard.reporting.domain.PerformanceReport;
import com.cscreativ.billboard.reporting.domain.ReportType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportingController {

    private final ReportingService reportingService;
    private final ReportingMapper reportingMapper;

    public ReportingController(ReportingService reportingService, ReportingMapper reportingMapper) {
        this.reportingService = reportingService;
        this.reportingMapper = reportingMapper;
    }

    @PostMapping("/generate")
    public ResponseEntity<PerformanceReportResponse> generateReport(@RequestBody GenerateReportRequest request) {
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
    public ResponseEntity<PerformanceReportResponse> getReportById(@PathVariable UUID id) {
        PerformanceReport report = reportingService.getReportById(id);
        return ResponseEntity.ok(reportingMapper.toResponse(report));
    }

    @GetMapping("/target/{targetId}")
    public ResponseEntity<List<PerformanceReportResponse>> getReportsByTarget(@PathVariable UUID targetId) {
        List<PerformanceReport> reports = reportingService.getReportsByTarget(targetId);
        return ResponseEntity.ok(reports.stream().map(reportingMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<PerformanceReportResponse>> getReportsByType(@PathVariable ReportType type) {
        List<PerformanceReport> reports = reportingService.getReportsByType(type);
        return ResponseEntity.ok(reports.stream().map(reportingMapper::toResponse).collect(Collectors.toList()));
    }
}
