package com.cscreativ.billboard.booking.domain.repository;

import com.cscreativ.billboard.booking.domain.Booking;
import com.cscreativ.billboard.booking.domain.BookingStatus;
import com.cscreativ.billboard.booking.domain.valueobject.DateRange;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(UUID id);
    List<Booking> findByBillboardId(UUID billboardId);
    List<Booking> findByAdvertiserId(UUID advertiserId);
    List<Booking> findByStatusIn(Collection<BookingStatus> statuses);
    boolean existsOverlappingBooking(UUID billboardId, DateRange period);
    List<Booking> findAll();
}
