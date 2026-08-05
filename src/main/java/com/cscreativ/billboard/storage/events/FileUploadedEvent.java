package com.cscreativ.billboard.storage.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record FileUploadedEvent(UUID fileId, String filename, UUID ownerId, LocalDateTime occurredOn) {}
