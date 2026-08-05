package com.cscreativ.billboard.payment.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentFailedEvent(UUID transactionId, UUID payerId, String failureReason, LocalDateTime occurredOn) {}
