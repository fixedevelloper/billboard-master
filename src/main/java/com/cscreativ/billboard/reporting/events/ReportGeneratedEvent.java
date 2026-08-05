package com.cscreativ.billboard.reporting.events;

import com.cscreativ.billboard.reporting.domain.ReportType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReportGeneratedEvent(UUID reportId, UUID targetId, ReportType type, LocalDateTime occurredOn) {}
