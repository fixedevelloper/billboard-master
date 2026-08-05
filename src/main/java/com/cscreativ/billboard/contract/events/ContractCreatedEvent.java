package com.cscreativ.billboard.contract.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContractCreatedEvent(UUID contractId, UUID bookingId, LocalDateTime occurredOn) {}
