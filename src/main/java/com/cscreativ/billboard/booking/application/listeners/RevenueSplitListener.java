package com.cscreativ.billboard.booking.application.listeners;

import com.cscreativ.billboard.billboard.BillboardFacade;
import com.cscreativ.billboard.booking.domain.Booking;
import com.cscreativ.billboard.booking.domain.repository.BookingRepository;
import com.cscreativ.billboard.owner.OwnerFacade;
import com.cscreativ.billboard.payment.events.PaymentCompletedEvent;
import com.cscreativ.billboard.wallet.WalletFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Répartit chaque paiement de réservation réussi entre le portefeuille du propriétaire du
 * panneau et le portefeuille plateforme (commission), selon BillboardOwner.revenueShareRate.
 * Vit dans booking (pas payment) pour la même raison que PaymentEventListener : booking dépend
 * déjà de payment, donc payment ne peut pas dépendre de booking en retour.
 */
@Component
public class RevenueSplitListener {

    private static final Logger log = LoggerFactory.getLogger(RevenueSplitListener.class);

    private final BookingRepository bookingRepository;
    private final BillboardFacade billboardFacade;
    private final OwnerFacade ownerFacade;
    private final WalletFacade walletFacade;
    private final UUID platformWalletUserId;

    public RevenueSplitListener(BookingRepository bookingRepository,
                                 BillboardFacade billboardFacade,
                                 OwnerFacade ownerFacade,
                                 WalletFacade walletFacade,
                                 @Value("${platform.wallet.user-id}") UUID platformWalletUserId) {
        this.bookingRepository = bookingRepository;
        this.billboardFacade = billboardFacade;
        this.ownerFacade = ownerFacade;
        this.walletFacade = walletFacade;
        this.platformWalletUserId = platformWalletUserId;
    }

    @ApplicationModuleListener
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        Booking booking = bookingRepository.findById(event.referenceId()).orElse(null);
        if (booking == null) {
            log.warn("Paiement {} complété : réservation {} introuvable, répartition ignorée", event.transactionId(), event.referenceId());
            return;
        }
        UUID ownerId = billboardFacade.findOwnerIdByBillboard(booking.getBillboardId()).orElse(null);
        if (ownerId == null) {
            log.warn("Paiement {} complété : propriétaire introuvable pour le panneau {}, répartition ignorée", event.transactionId(), booking.getBillboardId());
            return;
        }
        UUID ownerUserId = ownerFacade.findUserIdByOwnerId(ownerId).orElse(null);
        if (ownerUserId == null) {
            log.warn("Paiement {} complété : propriétaire {} sans compte utilisateur, répartition ignorée", event.transactionId(), ownerId);
            return;
        }

        BigDecimal rate = ownerFacade.findRevenueShareRateByOwnerId(ownerId).orElse(BigDecimal.ZERO);
        // Garde-fou défensif : la borne [0,1] est validée à l'écriture (BillboardOwner) mais
        // d'anciennes données pourraient encore violer cette contrainte.
        rate = rate.max(BigDecimal.ZERO).min(BigDecimal.ONE);

        BigDecimal amount = event.amount();
        BigDecimal platformCut = amount.multiply(rate).setScale(4, RoundingMode.HALF_UP);
        BigDecimal ownerCut = amount.subtract(platformCut);
        String currency = booking.getCurrency();

        walletFacade.creditIfAbsent(ownerUserId, ownerCut, currency, "payment:" + event.transactionId() + ":owner");
        walletFacade.creditIfAbsent(platformWalletUserId, platformCut, currency, "payment:" + event.transactionId() + ":platform");
    }
}
