package com.cscreativ.billboard.reporting.application;

import com.cscreativ.billboard.reporting.ReportingFacade;
import com.cscreativ.billboard.reporting.domain.PerformanceReport;
import com.cscreativ.billboard.reporting.domain.repository.PerformanceReportRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ReportingFacadeImpl implements ReportingFacade {

    private final PerformanceReportRepository reportRepository;

    public ReportingFacadeImpl(PerformanceReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Optional<PerformanceReport> findReportById(UUID reportId) {
        return reportRepository.findById(reportId);
    }
}
