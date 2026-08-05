package com.cscreativ.billboard.billboard.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record BillboardStatusChangedEvent(UUID billboardId, String newStatus, LocalDateTime occurredOn) {}
