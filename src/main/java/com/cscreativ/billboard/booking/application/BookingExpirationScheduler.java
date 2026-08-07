package com.cscreativ.billboard.booking.application;

import com.cscreativ.billboard.booking.domain.Booking;
import com.cscreativ.billboard.booking.domain.BookingStatus;
import com.cscreativ.billboard.booking.domain.repository.BookingRepository;
import com.cscreativ.billboard.booking.events.BookingExpiredEvent;
import com.cscreativ.billboard.payment.PaymentFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Fait expirer automatiquement les réservations restées trop longtemps sans paiement
 * réussi. Le délai (en jours, à partir de la création de la réservation) est configurable
 * via BOOKING_UNPAID_EXPIRATION_DAYS (voir .env / application.properties). Le paiement de
 * la location référence directement la réservation (referenceId = bookingId), d'où la
 * vérification via PaymentFacade.
 */
@Component
public class BookingExpirationScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingExpirationScheduler.class);

    private static final List<BookingStatus> EXPIRABLE_STATUSES = List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final BookingRepository bookingRepository;
    private final PaymentFacade paymentFacade;
    private final ApplicationEventPublisher eventPublisher;
    private final int unpaidExpirationDays;

    public BookingExpirationScheduler(BookingRepository bookingRepository,
                                       PaymentFacade paymentFacade,
                                       ApplicationEventPublisher eventPublisher,
                                       @Value("${booking.unpaid-expiration-days}") int unpaidExpirationDays) {
        this.bookingRepository = bookingRepository;
        this.paymentFacade = paymentFacade;
        this.eventPublisher = eventPublisher;
        this.unpaidExpirationDays = unpaidExpirationDays;
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void expireUnpaidBookings() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(unpaidExpirationDays);
        List<Booking> candidates = bookingRepository.findByStatusIn(EXPIRABLE_STATUSES);

        int expiredCount = 0;
        for (Booking booking : candidates) {
            if (booking.getCreatedAt().isAfter(threshold)) {
                continue;
            }
            if (hasCompletedPayment(booking)) {
                continue;
            }

            booking.expire();
            bookingRepository.save(booking);
            eventPublisher.publishEvent(new BookingExpiredEvent(booking.getId(), booking.getBillboardId(), booking.getAdvertiserId(), LocalDateTime.now()));
            expiredCount++;
        }

        if (expiredCount > 0) {
            log.info("{} réservation(s) expirée(s) faute de paiement sous {} jour(s)", expiredCount, unpaidExpirationDays);
        }
    }

    private boolean hasCompletedPayment(Booking booking) {
        return paymentFacade.hasCompletedPaymentForReference(booking.getId());
    }
}
