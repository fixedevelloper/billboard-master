package com.cscreativ.billboard.reporting;

import com.cscreativ.billboard.reporting.domain.PerformanceReport;

import java.util.Optional;
import java.util.UUID;

public interface ReportingFacade {
    Optional<PerformanceReport> findReportById(UUID reportId);
}
