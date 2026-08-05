package com.cscreativ.billboard.payment.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentCompletedEvent(UUID transactionId, UUID payerId, UUID referenceId, BigDecimal amount, String gatewayReference, LocalDateTime occurredOn) {}
