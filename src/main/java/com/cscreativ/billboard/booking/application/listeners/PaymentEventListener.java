package com.cscreativ.billboard.booking.application.listeners;

import com.cscreativ.billboard.billboard.BillboardFacade;
import com.cscreativ.billboard.booking.domain.Booking;
import com.cscreativ.billboard.booking.domain.repository.BookingRepository;
import com.cscreativ.billboard.notification.NotificationFacade;
import com.cscreativ.billboard.owner.OwnerFacade;
import com.cscreativ.billboard.payment.events.PaymentCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Prévient le propriétaire du panneau qu'un paiement a été reçu pour l'une de ses réservations.
 * Vit dans le module booking (pas payment) pour éviter un cycle : booking dépend déjà de
 * payment (BookingExpirationScheduler), payment ne peut donc pas dépendre de booking en retour —
 * réagir à l'événement publié par payment est la façon idiomatique de fermer la boucle sans cycle.
 */
@Component
public class PaymentEventListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);

    private final BookingRepository bookingRepository;
    private final BillboardFacade billboardFacade;
    private final OwnerFacade ownerFacade;
    private final NotificationFacade notificationFacade;

    public PaymentEventListener(BookingRepository bookingRepository, BillboardFacade billboardFacade,
                                 OwnerFacade ownerFacade, NotificationFacade notificationFacade) {
        this.bookingRepository = bookingRepository;
        this.billboardFacade = billboardFacade;
        this.ownerFacade = ownerFacade;
        this.notificationFacade = notificationFacade;
    }

    @ApplicationModuleListener
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        Booking booking = bookingRepository.findById(event.referenceId()).orElse(null);
        if (booking == null) {
            log.warn("Paiement {} complété : réservation {} introuvable, notification non envoyée", event.transactionId(), event.referenceId());
            return;
        }
        UUID ownerId = billboardFacade.findOwnerIdByBillboard(booking.getBillboardId()).orElse(null);
        if (ownerId == null) {
            log.warn("Paiement {} complété : propriétaire introuvable pour le panneau {}, notification non envoyée", event.transactionId(), booking.getBillboardId());
            return;
        }
        Optional<UUID> recipientUserId = ownerFacade.findUserIdByOwnerId(ownerId);
        Optional<String> recipientEmail = ownerFacade.findContactEmailByOwnerId(ownerId);
        if (recipientUserId.isEmpty() || recipientEmail.isEmpty()) {
            log.warn("Paiement {} complété : propriétaire {} sans compte utilisateur, notification non envoyée", event.transactionId(), ownerId);
            return;
        }

        Map<String, String> params = new LinkedHashMap<>();
        params.put("Panneau", billboardFacade.findTitleByBillboardId(booking.getBillboardId()).orElse("Panneau"));
        params.put("Montant", event.amount() + " " + booking.getCurrency());
        params.put("Référence de réservation", booking.getId().toString());
        notificationFacade.sendNotification(recipientUserId.get(), recipientEmail.get(), "PAYMENT_RECEIVED", "EMAIL", params);
    }
}
