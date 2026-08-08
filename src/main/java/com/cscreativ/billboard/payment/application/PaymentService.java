package com.cscreativ.billboard.payment.application;

import com.cscreativ.billboard.advertiser.AdvertiserFacade;
import com.cscreativ.billboard.contract.ContractFacade;
import com.cscreativ.billboard.mediabuyer.MediaBuyerFacade;
import com.cscreativ.billboard.notification.NotificationFacade;
import com.cscreativ.billboard.payment.domain.PaymentMethod;
import com.cscreativ.billboard.payment.domain.PaymentStatus;
import com.cscreativ.billboard.payment.domain.PaymentTransaction;
import com.cscreativ.billboard.payment.domain.exception.PaymentTransactionNotFoundException;
import com.cscreativ.billboard.payment.domain.repository.PaymentTransactionRepository;
import com.cscreativ.billboard.payment.domain.valueobject.Money;
import com.cscreativ.billboard.payment.events.PaymentCompletedEvent;
import com.cscreativ.billboard.payment.events.PaymentFailedEvent;
import com.cscreativ.billboard.payment.events.PaymentInitiatedEvent;
import com.cscreativ.billboard.payment.infrastructure.gateway.FlutterwaveClient;
import com.cscreativ.billboard.payment.infrastructure.gateway.FlutterwaveException;
import com.cscreativ.billboard.payment.infrastructure.gateway.FlutterwaveVerificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentTransactionRepository transactionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ContractFacade contractFacade;
    private final FlutterwaveClient flutterwaveClient;
    private final AdvertiserFacade advertiserFacade;
    private final MediaBuyerFacade mediaBuyerFacade;
    private final NotificationFacade notificationFacade;
    private final String backendUrl;
    private final String frontendUrl;

    public PaymentService(PaymentTransactionRepository transactionRepository, ApplicationEventPublisher eventPublisher,
                           ContractFacade contractFacade, FlutterwaveClient flutterwaveClient,
                           AdvertiserFacade advertiserFacade, MediaBuyerFacade mediaBuyerFacade,
                           NotificationFacade notificationFacade,
                           @Value("${app.backend-url}") String backendUrl,
                           @Value("${app.frontend-url}") String frontendUrl) {
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
        this.contractFacade = contractFacade;
        this.flutterwaveClient = flutterwaveClient;
        this.advertiserFacade = advertiserFacade;
        this.mediaBuyerFacade = mediaBuyerFacade;
        this.notificationFacade = notificationFacade;
        this.backendUrl = backendUrl;
        this.frontendUrl = frontendUrl;
    }

    /**
     * referenceId désigne ici la réservation (bookingId) : le paiement porte sur la location
     * du panneau, réglée avant la création de la campagne (contrat signé → paiement → campagne).
     * initiatedBy est l'annonceur à l'origine de l'appel ; quand il diffère de payerId,
     * l'annonceur délègue le règlement à un media buyer, qui est alors notifié (invitation
     * à finaliser le paiement) — voir notifyDelegatedPayer.
     */
    @Transactional
    public PaymentTransaction initiatePayment(UUID payerId, UUID referenceId, BigDecimal amount, String currency, PaymentMethod paymentMethod, UUID initiatedBy) {
        if (!contractFacade.isSignedForBooking(referenceId)) {
            throw new IllegalStateException("Le paiement ne peut être initié tant que le contrat n'est pas signé par les deux parties");
        }

        Money money = new Money(amount, currency);
        PaymentTransaction transaction = PaymentTransaction.initiate(payerId, referenceId, money, paymentMethod, initiatedBy);
        PaymentTransaction saved = transactionRepository.save(transaction);

        eventPublisher.publishEvent(new PaymentInitiatedEvent(
                saved.getId(), saved.getPayerId(), saved.getReferenceId(), saved.getMoney().getAmount(), saved.getMoney().getCurrency(), LocalDateTime.now()
        ));

        if (saved.isDelegated()) {
            notifyDelegatedPayer(saved);
        }
        return saved;
    }

    @Transactional
    public void completePayment(UUID transactionId, String gatewayReference) {
        PaymentTransaction transaction = getTransactionById(transactionId);
        transaction.markAsSuccessful(gatewayReference);
        PaymentTransaction saved = transactionRepository.save(transaction);

        eventPublisher.publishEvent(new PaymentCompletedEvent(
                saved.getId(), saved.getPayerId(), saved.getReferenceId(), saved.getMoney().getAmount(), gatewayReference, LocalDateTime.now()
        ));

        if (saved.isDelegated()) {
            notifyDelegatingAdvertiser(saved);
        }
    }

    /** Invite le media buyer choisi par l'annonceur à finaliser le paiement délégué. */
    private void notifyDelegatedPayer(PaymentTransaction transaction) {
        UUID mediaBuyerId = transaction.getPayerId();
        Optional<UUID> recipientUserId = mediaBuyerFacade.findUserIdByBuyerId(mediaBuyerId);
        Optional<String> recipientEmail = mediaBuyerFacade.findContactEmailByBuyerId(mediaBuyerId);
        if (recipientUserId.isEmpty() || recipientEmail.isEmpty()) {
            log.warn("Paiement {} délégué à un media buyer introuvable ({}), notification non envoyée", transaction.getId(), mediaBuyerId);
            return;
        }
        String advertiserName = advertiserFacade.findCompanyNameByAdvertiserId(transaction.getInitiatedBy()).orElse("Un annonceur");

        Map<String, String> params = new LinkedHashMap<>();
        params.put("Annonceur", advertiserName);
        params.put("Montant", transaction.getMoney().getAmount() + " " + transaction.getMoney().getCurrency());
        params.put("Référence de réservation", transaction.getReferenceId().toString());

        notificationFacade.sendNotification(recipientUserId.get(), recipientEmail.get(),
                "PAYMENT_DELEGATION_REQUEST", "IN_APP", params);
    }

    /** Prévient l'annonceur que le media buyer qu'il a invité a bien finalisé le paiement. */
    private void notifyDelegatingAdvertiser(PaymentTransaction transaction) {
        UUID advertiserId = transaction.getInitiatedBy();
        Optional<UUID> recipientUserId = advertiserFacade.findUserIdByAdvertiserId(advertiserId);
        Optional<String> recipientEmail = advertiserFacade.findContactEmailByAdvertiserId(advertiserId);
        if (recipientUserId.isEmpty() || recipientEmail.isEmpty()) {
            log.warn("Paiement {} finalisé mais annonceur délégant introuvable ({}), notification non envoyée", transaction.getId(), advertiserId);
            return;
        }
        String mediaBuyerName = mediaBuyerFacade.findCompanyNameByBuyerId(transaction.getPayerId()).orElse("Votre media buyer");

        Map<String, String> params = new LinkedHashMap<>();
        params.put("Media buyer", mediaBuyerName);
        params.put("Montant", transaction.getMoney().getAmount() + " " + transaction.getMoney().getCurrency());
        params.put("Référence de réservation", transaction.getReferenceId().toString());

        notificationFacade.sendNotification(recipientUserId.get(), recipientEmail.get(),
                "PAYMENT_DELEGATION_COMPLETED", "IN_APP", params);
    }

    @Transactional
    public void failPayment(UUID transactionId, String reason) {
        PaymentTransaction transaction = getTransactionById(transactionId);
        transaction.markAsFailed(reason);
        PaymentTransaction saved = transactionRepository.save(transaction);

        eventPublisher.publishEvent(new PaymentFailedEvent(saved.getId(), saved.getPayerId(), reason, LocalDateTime.now()));
    }

    /**
     * Lance le paiement hébergé Flutterwave pour une transaction déjà initiée (PENDING) et
     * renvoie l'URL de checkout vers laquelle rediriger le navigateur. tx_ref = l'id de la
     * transaction locale, ce qui permet de la retrouver sans état supplémentaire lors du
     * retour (callback) ou du webhook.
     */
    public String initiateFlutterwaveCheckout(UUID transactionId, String customerEmail, String customerName, String customerPhone) {
        PaymentTransaction transaction = getTransactionById(transactionId);
        if (transaction.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Cette transaction n'est plus en attente de paiement");
        }
        String redirectUrl = backendUrl + "/api/v1/payments/flutterwave/callback";
        return flutterwaveClient.initializePayment(
                transactionId.toString(),
                transaction.getMoney().getAmount(),
                transaction.getMoney().getCurrency(),
                redirectUrl,
                customerEmail,
                customerName,
                customerPhone
        );
    }

    /**
     * Appelé depuis la redirection navigateur (GET /flutterwave/callback) après le paiement.
     * Vérifie systématiquement la transaction auprès de Flutterwave avant de conclure — jamais
     * confiance dans le statut renvoyé tel quel par la query string, qui n'est qu'indicatif.
     * Retourne le bookingId (referenceId) pour construire la redirection finale vers le frontend.
     */
    @Transactional
    public UUID handleFlutterwaveRedirect(UUID transactionId, String flutterwaveTransactionId, boolean reportedSuccess) {
        PaymentTransaction transaction = getTransactionById(transactionId);
        if (transaction.getStatus() == PaymentStatus.PENDING) {
            if (reportedSuccess && flutterwaveTransactionId != null) {
                verifyAndFinalize(transactionId, flutterwaveTransactionId);
            } else {
                failPayment(transactionId, "Paiement Flutterwave annulé ou échoué");
            }
        }
        return transaction.getReferenceId();
    }

    /** Appelé depuis le webhook Flutterwave (déjà authentifié via le header verif-hash côté contrôleur). */
    @Transactional
    public void handleFlutterwaveWebhook(String flutterwaveTransactionId, String txRef) {
        if (txRef == null || flutterwaveTransactionId == null) {
            return;
        }
        UUID transactionId;
        try {
            transactionId = UUID.fromString(txRef);
        } catch (IllegalArgumentException e) {
            return;
        }
        verifyAndFinalize(transactionId, flutterwaveTransactionId);
    }

    private void verifyAndFinalize(UUID transactionId, String flutterwaveTransactionId) {
        PaymentTransaction transaction = getTransactionById(transactionId);
        if (transaction.getStatus() != PaymentStatus.PENDING) {
            return; // déjà traité (redirection + webhook peuvent arriver dans n'importe quel ordre)
        }
        try {
            FlutterwaveVerificationResult result = flutterwaveClient.verifyTransaction(flutterwaveTransactionId);
            boolean amountMatches = result.amount() != null
                    && result.amount().compareTo(transaction.getMoney().getAmount()) >= 0
                    && transaction.getMoney().getCurrency().equalsIgnoreCase(result.currency());
            if (result.successful() && amountMatches) {
                completePayment(transactionId, flutterwaveTransactionId);
            } else {
                failPayment(transactionId, "La vérification Flutterwave a échoué");
            }
        } catch (FlutterwaveException e) {
            failPayment(transactionId, "Erreur lors de la vérification Flutterwave : " + e.getMessage());
        }
    }

    public String getFrontendUrl() {
        return frontendUrl;
    }

    public PaymentTransaction getTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new PaymentTransactionNotFoundException("Transaction de paiement non trouvée avec l'id : " + id));
    }

    public List<PaymentTransaction> getTransactionsByPayer(UUID payerId) {
        return transactionRepository.findByPayerId(payerId);
    }

    public List<PaymentTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<PaymentTransaction> getTransactionsByReference(UUID referenceId) {
        return transactionRepository.findByReferenceId(referenceId);
    }
}
