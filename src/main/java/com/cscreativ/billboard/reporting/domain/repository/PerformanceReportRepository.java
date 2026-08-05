package com.cscreativ.billboard.reporting.domain.repository;

import com.cscreativ.billboard.reporting.domain.PerformanceReport;
import com.cscreativ.billboard.reporting.domain.ReportType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PerformanceReportRepository {
    PerformanceReport save(PerformanceReport report);
    Optional<PerformanceReport> findById(UUID id);
    List<PerformanceReport> findByTargetId(UUID targetId);
    List<PerformanceReport> findByType(ReportType type);
}
