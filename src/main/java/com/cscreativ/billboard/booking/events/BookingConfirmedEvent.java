package com.cscreativ.billboard.booking.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingConfirmedEvent(UUID bookingId, UUID billboardId, UUID advertiserId, LocalDateTime occurredOn) {}
