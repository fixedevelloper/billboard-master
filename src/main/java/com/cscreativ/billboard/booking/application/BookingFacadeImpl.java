package com.cscreativ.billboard.booking.application;

import com.cscreativ.billboard.booking.BookingFacade;
import com.cscreativ.billboard.booking.domain.Booking;
import com.cscreativ.billboard.booking.domain.repository.BookingRepository;
import com.cscreativ.billboard.booking.domain.valueobject.DateRange;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Component
public class BookingFacadeImpl implements BookingFacade {

    private final BookingRepository bookingRepository;

    public BookingFacadeImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Optional<Booking> findBookingById(UUID bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    public boolean isBillboardAvailableForPeriod(UUID billboardId, LocalDate startDate, LocalDate endDate) {
        return !bookingRepository.existsOverlappingBooking(billboardId, new DateRange(startDate, endDate));
    }

    @Override
    public Optional<UUID> findBillboardIdByBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId).map(Booking::getBillboardId);
    }

    @Override
    public Optional<UUID> findAdvertiserIdByBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId).map(Booking::getAdvertiserId);
    }

    @Override
    public Optional<LocalDate> findPeriodEndDateByBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId).map(booking -> booking.getPeriod().getEndDate());
    }
}
