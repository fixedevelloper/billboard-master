package com.cscreativ.billboard.reporting.infrastructure.persistence;

import com.cscreativ.billboard.reporting.domain.PerformanceReport;
import com.cscreativ.billboard.reporting.domain.ReportType;
import com.cscreativ.billboard.reporting.domain.repository.PerformanceReportRepository;
import com.cscreativ.billboard.reporting.domain.valueobject.ReportMetrics;
import com.cscreativ.billboard.reporting.domain.valueobject.ReportPeriod;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PerformanceReportPersistenceAdapter implements PerformanceReportRepository {

    private final PerformanceReportJpaRepository jpaRepository;

    public PerformanceReportPersistenceAdapter(PerformanceReportJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PerformanceReport save(PerformanceReport report) {
        PerformanceReportEntity entity = toEntity(report);
        PerformanceReportEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PerformanceReport> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<PerformanceReport> findByTargetId(UUID targetId) {
        return jpaRepository.findByTargetId(targetId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<PerformanceReport> findByType(ReportType type) {
        return jpaRepository.findByType(type).stream().map(this::toDomain).collect(Collectors.toList());
    }

    private PerformanceReportEntity toEntity(PerformanceReport domain) {
        PerformanceReportEntity entity = new PerformanceReportEntity();
        entity.setId(domain.getId());
        entity.setTargetId(domain.getTargetId());
        entity.setType(domain.getType());
        entity.setStartDate(domain.getPeriod().getStartDate());
        entity.setEndDate(domain.getPeriod().getEndDate());
        entity.setTotalImpressions(domain.getMetrics().getTotalImpressions());
        entity.setTotalInteractions(domain.getMetrics().getTotalInteractions());
        entity.setTotalRevenue(domain.getMetrics().getTotalRevenue());
        entity.setOccupancyRate(domain.getMetrics().getOccupancyRate());
        entity.setGeneratedAt(domain.getGeneratedAt());
        return entity;
    }

    private PerformanceReport toDomain(PerformanceReportEntity entity) {
        ReportPeriod period = new ReportPeriod(entity.getStartDate(), entity.getEndDate());
        ReportMetrics metrics = new ReportMetrics(
                entity.getTotalImpressions(),
                entity.getTotalInteractions(),
                entity.getTotalRevenue(),
                entity.getOccupancyRate()
        );

        return new PerformanceReport(
                entity.getId(),
                entity.getTargetId(),
                entity.getType(),
                period,
                metrics,
                entity.getGeneratedAt()
        );
    }
}
