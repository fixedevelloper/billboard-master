package com.cscreativ.billboard.booking;

import com.cscreativ.billboard.booking.domain.Booking;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface BookingFacade {
    Optional<Booking> findBookingById(UUID bookingId);
    boolean isBillboardAvailableForPeriod(UUID billboardId, LocalDate startDate, LocalDate endDate);
}
