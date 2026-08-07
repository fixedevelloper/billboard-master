package com.cscreativ.billboard.booking.application;

import com.cscreativ.billboard.booking.domain.Booking;
import com.cscreativ.billboard.booking.domain.exception.BillboardUnavailableException;
import com.cscreativ.billboard.booking.domain.exception.BookingNotFoundException;
import com.cscreativ.billboard.booking.domain.repository.BookingRepository;
import com.cscreativ.billboard.booking.domain.valueobject.DateRange;
import com.cscreativ.billboard.booking.events.BookingCancelledEvent;
import com.cscreativ.billboard.booking.events.BookingConfirmedEvent;
import com.cscreativ.billboard.booking.events.BookingCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final int unpaidExpirationDays;

    public BookingService(BookingRepository bookingRepository, ApplicationEventPublisher eventPublisher,
                           @Value("${booking.unpaid-expiration-days}") int unpaidExpirationDays) {
        this.bookingRepository = bookingRepository;
        this.eventPublisher = eventPublisher;
        this.unpaidExpirationDays = unpaidExpirationDays;
    }

    @Transactional
    public Booking createBooking(UUID billboardId, UUID advertiserId, LocalDate startDate, LocalDate endDate, BigDecimal dailyRate, String currency) {
        DateRange period = new DateRange(startDate, endDate);

        if (bookingRepository.existsOverlappingBooking(billboardId, period)) {
            throw new BillboardUnavailableException("Le panneau n'est pas disponible pour la période sélectionnée");
        }

        Booking booking = Booking.create(billboardId, advertiserId, period, dailyRate, currency);
        Booking saved = bookingRepository.save(booking);

        eventPublisher.publishEvent(new BookingCreatedEvent(saved.getId(), saved.getBillboardId(), saved.getAdvertiserId(), saved.getTotalPrice(), LocalDateTime.now()));
        return saved;
    }

    @Transactional
    public void confirmBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Réservation non trouvée avec l'id : " + bookingId));

        booking.confirm();
        bookingRepository.save(booking);

        eventPublisher.publishEvent(new BookingConfirmedEvent(booking.getId(), booking.getBillboardId(), booking.getAdvertiserId(), LocalDateTime.now()));
    }

    @Transactional
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Réservation non trouvée avec l'id : " + bookingId));

        booking.cancel();
        bookingRepository.save(booking);

        eventPublisher.publishEvent(new BookingCancelledEvent(booking.getId(), booking.getBillboardId(), LocalDateTime.now()));
    }

    public Booking getBookingById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Réservation non trouvée avec l'id : " + id));
    }

    public List<Booking> getBookingsByAdvertiser(UUID advertiserId) {
        return bookingRepository.findByAdvertiserId(advertiserId);
    }

    public List<Booking> getBookingsByBillboard(UUID billboardId) {
        return bookingRepository.findByBillboardId(billboardId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public int getUnpaidExpirationDays() {
        return unpaidExpirationDays;
    }
}
