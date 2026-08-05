package com.cscreativ.billboard.reporting.application;

import com.cscreativ.billboard.reporting.domain.PerformanceReport;
import com.cscreativ.billboard.reporting.domain.ReportType;
import com.cscreativ.billboard.reporting.domain.exception.ReportNotFoundException;
import com.cscreativ.billboard.reporting.domain.repository.PerformanceReportRepository;
import com.cscreativ.billboard.reporting.domain.valueobject.ReportMetrics;
import com.cscreativ.billboard.reporting.domain.valueobject.ReportPeriod;
import com.cscreativ.billboard.reporting.events.ReportGeneratedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReportingService {

    private final PerformanceReportRepository reportRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReportingService(PerformanceReportRepository reportRepository, ApplicationEventPublisher eventPublisher) {
        this.reportRepository = reportRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PerformanceReport generateReport(UUID targetId, ReportType type, LocalDateTime startDate, LocalDateTime endDate, long impressions, long interactions, BigDecimal revenue, BigDecimal occupancyRate) {
        ReportPeriod period = new ReportPeriod(startDate, endDate);
        ReportMetrics metrics = new ReportMetrics(impressions, interactions, revenue, occupancyRate);

        PerformanceReport report = PerformanceReport.generate(targetId, type, period, metrics);
        PerformanceReport saved = reportRepository.save(report);

        eventPublisher.publishEvent(new ReportGeneratedEvent(saved.getId(), saved.getTargetId(), saved.getType(), LocalDateTime.now()));
        return saved;
    }

    public PerformanceReport getReportById(UUID id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException("Rapport non trouvé avec l'id : " + id));
    }

    public List<PerformanceReport> getReportsByTarget(UUID targetId) {
        return reportRepository.findByTargetId(targetId);
    }

    public List<PerformanceReport> getReportsByType(ReportType type) {
        return reportRepository.findByType(type);
    }
}
