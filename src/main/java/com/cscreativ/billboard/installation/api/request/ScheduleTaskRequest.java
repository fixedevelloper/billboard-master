package com.cscreativ.billboard.installation.api.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleTaskRequest(
        UUID campaignId,
        UUID billboardId,
        UUID technicianId,
        LocalDateTime scheduledDate
) {}
