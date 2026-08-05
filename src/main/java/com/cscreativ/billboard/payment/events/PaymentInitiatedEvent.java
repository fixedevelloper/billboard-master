package com.cscreativ.billboard.payment.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentInitiatedEvent(UUID transactionId, UUID payerId, UUID referenceId, BigDecimal amount, String currency, LocalDateTime occurredOn) {}
