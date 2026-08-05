package com.cscreativ.billboard.reporting.api.mapper;

import com.cscreativ.billboard.reporting.api.response.PerformanceReportResponse;
import com.cscreativ.billboard.reporting.domain.PerformanceReport;
import org.springframework.stereotype.Component;

@Component
public class ReportingMapper {

    public PerformanceReportResponse toResponse(PerformanceReport report) {
        return new PerformanceReportResponse(
                report.getId(),
                report.getTargetId(),
                report.getType().name(),
                report.getPeriod().getStartDate(),
                report.getPeriod().getEndDate(),
                report.getMetrics().getTotalImpressions(),
                report.getMetrics().getTotalInteractions(),
                report.getMetrics().getTotalRevenue(),
                report.getMetrics().getOccupancyRate(),
                report.getGeneratedAt()
        );
    }
}
