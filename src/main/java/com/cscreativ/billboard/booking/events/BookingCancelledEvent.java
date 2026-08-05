package com.cscreativ.billboard.booking.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingCancelledEvent(UUID bookingId, UUID billboardId, LocalDateTime occurredOn) {}
