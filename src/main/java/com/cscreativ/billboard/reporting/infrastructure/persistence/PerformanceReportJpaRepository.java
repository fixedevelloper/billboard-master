package com.cscreativ.billboard.reporting.infrastructure.persistence;

import com.cscreativ.billboard.reporting.domain.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PerformanceReportJpaRepository extends JpaRepository<PerformanceReportEntity, UUID> {
    List<PerformanceReportEntity> findByTargetId(UUID targetId);
    List<PerformanceReportEntity> findByType(ReportType type);
}
