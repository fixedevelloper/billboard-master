package com.cscreativ.billboard.booking;

import com.cscreativ.billboard.booking.domain.Booking;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface BookingFacade {
    Optional<Booking> findBookingById(UUID bookingId);
    boolean isBillboardAvailableForPeriod(UUID billboardId, LocalDate startDate, LocalDate endDate);
    Optional<UUID> findBillboardIdByBooking(UUID bookingId);
    Optional<UUID> findAdvertiserIdByBooking(UUID bookingId);

    /**
     * LocalDate brut (pas DateRange, interne au module) : permet à d'autres modules de comparer
     * la période de réservation à la date du jour sans dépendre d'un type non exposé (voir
     * campaign.application.CampaignCompletionScheduler).
     */
    Optional<LocalDate> findPeriodEndDateByBooking(UUID bookingId);
}
