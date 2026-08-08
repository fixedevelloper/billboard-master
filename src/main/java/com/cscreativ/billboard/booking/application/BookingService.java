package com.cscreativ.billboard.booking.application;

import com.cscreativ.billboard.advertiser.AdvertiserFacade;
import com.cscreativ.billboard.billboard.BillboardFacade;
import com.cscreativ.billboard.booking.domain.Booking;
import com.cscreativ.billboard.booking.domain.exception.BillboardUnavailableException;
import com.cscreativ.billboard.booking.domain.exception.BookingNotFoundException;
import com.cscreativ.billboard.booking.domain.repository.BookingRepository;
import com.cscreativ.billboard.booking.domain.valueobject.DateRange;
import com.cscreativ.billboard.booking.events.BookingCancelledEvent;
import com.cscreativ.billboard.booking.events.BookingConfirmedEvent;
import com.cscreativ.billboard.booking.events.BookingCreatedEvent;
import com.cscreativ.billboard.notification.NotificationFacade;
import com.cscreativ.billboard.owner.OwnerFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final BillboardFacade billboardFacade;
    private final OwnerFacade ownerFacade;
    private final AdvertiserFacade advertiserFacade;
    private final NotificationFacade notificationFacade;
    private final int unpaidExpirationDays;

    public BookingService(BookingRepository bookingRepository, ApplicationEventPublisher eventPublisher,
                           BillboardFacade billboardFacade, OwnerFacade ownerFacade, AdvertiserFacade advertiserFacade,
                           NotificationFacade notificationFacade,
                           @Value("${booking.unpaid-expiration-days}") int unpaidExpirationDays) {
        this.bookingRepository = bookingRepository;
        this.eventPublisher = eventPublisher;
        this.billboardFacade = billboardFacade;
        this.ownerFacade = ownerFacade;
        this.advertiserFacade = advertiserFacade;
        this.notificationFacade = notificationFacade;
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

        Map<String, String> params = bookingParams(saved);
        notifyOwner(saved, "BOOKING_CREATED", params);
        return saved;
    }

    @Transactional
    public void confirmBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Réservation non trouvée avec l'id : " + bookingId));

        booking.confirm();
        bookingRepository.save(booking);

        eventPublisher.publishEvent(new BookingConfirmedEvent(booking.getId(), booking.getBillboardId(), booking.getAdvertiserId(), LocalDateTime.now()));

        notifyAdvertiser(booking, "BOOKING_CONFIRMED", bookingParams(booking));
    }

    @Transactional
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Réservation non trouvée avec l'id : " + bookingId));

        booking.cancel();
        bookingRepository.save(booking);

        eventPublisher.publishEvent(new BookingCancelledEvent(booking.getId(), booking.getBillboardId(), LocalDateTime.now()));

        Map<String, String> params = bookingParams(booking);
        notifyAdvertiser(booking, "BOOKING_CANCELLED", params);
        notifyOwner(booking, "BOOKING_CANCELLED", params);
    }

    private Map<String, String> bookingParams(Booking booking) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("Panneau", billboardFacade.findTitleByBillboardId(booking.getBillboardId()).orElse("Panneau"));
        params.put("Dates", booking.getPeriod().getStartDate() + " au " + booking.getPeriod().getEndDate());
        params.put("Total", booking.getTotalPrice() + " " + booking.getCurrency());
        return params;
    }

    private void notifyOwner(Booking booking, String templateCode, Map<String, String> params) {
        UUID ownerId = billboardFacade.findOwnerIdByBillboard(booking.getBillboardId()).orElse(null);
        if (ownerId == null) {
            return;
        }
        Optional<UUID> recipientUserId = ownerFacade.findUserIdByOwnerId(ownerId);
        Optional<String> recipientEmail = ownerFacade.findContactEmailByOwnerId(ownerId);
        if (recipientUserId.isEmpty() || recipientEmail.isEmpty()) {
            log.warn("Réservation {} : propriétaire introuvable ({}), notification {} non envoyée", booking.getId(), ownerId, templateCode);
            return;
        }
        notificationFacade.sendNotification(recipientUserId.get(), recipientEmail.get(), templateCode, "EMAIL", params);
    }

    private void notifyAdvertiser(Booking booking, String templateCode, Map<String, String> params) {
        Optional<UUID> recipientUserId = advertiserFacade.findUserIdByAdvertiserId(booking.getAdvertiserId());
        Optional<String> recipientEmail = advertiserFacade.findContactEmailByAdvertiserId(booking.getAdvertiserId());
        if (recipientUserId.isEmpty() || recipientEmail.isEmpty()) {
            log.warn("Réservation {} : annonceur introuvable ({}), notification {} non envoyée", booking.getId(), booking.getAdvertiserId(), templateCode);
            return;
        }
        notificationFacade.sendNotification(recipientUserId.get(), recipientEmail.get(), templateCode, "EMAIL", params);
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
