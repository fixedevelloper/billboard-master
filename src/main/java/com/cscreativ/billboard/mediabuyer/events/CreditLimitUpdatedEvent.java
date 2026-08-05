package com.cscreativ.billboard.mediabuyer.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreditLimitUpdatedEvent(UUID buyerId, BigDecimal newCreditLimit, LocalDateTime occurredOn) {}
