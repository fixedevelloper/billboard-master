package com.cscreativ.billboard.booking.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingExpiredEvent(UUID bookingId, UUID billboardId, UUID advertiserId, LocalDateTime occurredOn) {}
