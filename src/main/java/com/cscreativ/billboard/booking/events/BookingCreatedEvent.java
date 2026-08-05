package com.cscreativ.billboard.booking.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingCreatedEvent(UUID bookingId, UUID billboardId, UUID advertiserId, BigDecimal totalPrice, LocalDateTime occurredOn) {}
