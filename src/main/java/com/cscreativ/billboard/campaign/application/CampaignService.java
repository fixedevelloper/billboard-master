package com.cscreativ.billboard.campaign.application;

import com.cscreativ.billboard.advertiser.AdvertiserFacade;
import com.cscreativ.billboard.campaign.domain.Campaign;
import com.cscreativ.billboard.campaign.domain.CampaignStatus;
import com.cscreativ.billboard.campaign.domain.exception.CampaignNotFoundException;
import com.cscreativ.billboard.campaign.domain.repository.CampaignRepository;
import com.cscreativ.billboard.campaign.domain.valueobject.MediaAsset;
import com.cscreativ.billboard.campaign.events.CampaignActivatedEvent;
import com.cscreativ.billboard.campaign.events.CampaignApprovedEvent;
import com.cscreativ.billboard.campaign.events.CampaignCompletedEvent;
import com.cscreativ.billboard.campaign.events.CampaignRejectedEvent;
import com.cscreativ.billboard.campaign.events.CampaignSubmittedEvent;
import com.cscreativ.billboard.notification.NotificationFacade;
import com.cscreativ.billboard.payment.PaymentFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CampaignService {

    private static final Logger log = LoggerFactory.getLogger(CampaignService.class);

    private final CampaignRepository campaignRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PaymentFacade paymentFacade;
    private final AdvertiserFacade advertiserFacade;
    private final NotificationFacade notificationFacade;

    public CampaignService(CampaignRepository campaignRepository, ApplicationEventPublisher eventPublisher,
                            PaymentFacade paymentFacade, AdvertiserFacade advertiserFacade, NotificationFacade notificationFacade) {
        this.campaignRepository = campaignRepository;
        this.eventPublisher = eventPublisher;
        this.paymentFacade = paymentFacade;
        this.advertiserFacade = advertiserFacade;
        this.notificationFacade = notificationFacade;
    }

    /**
     * Le parcours attendu est réservation → contrat signé → paiement → campagne : la campagne
     * ne peut donc être créée qu'une fois le paiement de la réservation (referenceId = bookingId) réussi.
     */
    @Transactional
    public Campaign createCampaign(UUID bookingId, UUID advertiserId, String name, String description,
                                   String mediaUrl, String fileType, Long fileSize) {
        if (!paymentFacade.hasCompletedPaymentForReference(bookingId)) {
            throw new IllegalStateException("La campagne ne peut être créée tant que le paiement de la réservation n'est pas finalisé");
        }
        MediaAsset mediaAsset = (mediaUrl == null || fileType == null || fileSize == null)
                ? null
                : new MediaAsset(mediaUrl, fileType, fileSize);
        Campaign campaign = Campaign.create(bookingId, advertiserId, name, description, mediaAsset);
        return campaignRepository.save(campaign);
    }

    @Transactional
    public void submitCampaign(UUID campaignId) {
        Campaign campaign = getCampaignById(campaignId);
        campaign.submitForApproval();
        campaignRepository.save(campaign);

        eventPublisher.publishEvent(new CampaignSubmittedEvent(campaign.getId(), campaign.getAdvertiserId(), LocalDateTime.now()));
    }

    @Transactional
    public void approveCampaign(UUID campaignId) {
        Campaign campaign = getCampaignById(campaignId);
        campaign.approve();
        campaignRepository.save(campaign);

        eventPublisher.publishEvent(new CampaignApprovedEvent(campaign.getId(), campaign.getBookingId(), LocalDateTime.now()));

        Map<String, String> params = new LinkedHashMap<>();
        params.put("Campagne", campaign.getName());
        notifyAdvertiser(campaign, "CAMPAIGN_APPROVED", params);
    }

    @Transactional
    public void rejectCampaign(UUID campaignId, String reason) {
        Campaign campaign = getCampaignById(campaignId);
        campaign.reject(reason);
        campaignRepository.save(campaign);

        eventPublisher.publishEvent(new CampaignRejectedEvent(campaign.getId(), reason, LocalDateTime.now()));

        Map<String, String> params = new LinkedHashMap<>();
        params.put("Campagne", campaign.getName());
        params.put("Motif", reason);
        notifyAdvertiser(campaign, "CAMPAIGN_REJECTED", params);
    }

    /**
     * Appelée par CampaignFacade.activateCampaign (installation.application.InstallationService,
     * à la fin de la pose physique du visuel). Ignore silencieusement (log) une campagne qui n'est
     * pas APPROVED plutôt que de lever une exception qui ferait échouer la complétion de la tâche
     * d'installation elle-même.
     */
    @Transactional
    public void activateCampaign(UUID campaignId) {
        Campaign campaign = getCampaignById(campaignId);
        if (campaign.getStatus() != CampaignStatus.APPROVED) {
            log.warn("Installation complétée pour la campagne {} (statut {}) : activation ignorée, statut inattendu",
                    campaignId, campaign.getStatus());
            return;
        }
        campaign.activate();
        campaignRepository.save(campaign);

        eventPublisher.publishEvent(new CampaignActivatedEvent(campaign.getId(), campaign.getBookingId(), LocalDateTime.now()));

        Map<String, String> params = new LinkedHashMap<>();
        params.put("Campagne", campaign.getName());
        notifyAdvertiser(campaign, "CAMPAIGN_ACTIVE", params);
    }

    /** Déclenchée par CampaignCompletionScheduler une fois la période de réservation écoulée. */
    @Transactional
    public void completeCampaign(UUID campaignId) {
        Campaign campaign = getCampaignById(campaignId);
        campaign.complete();
        campaignRepository.save(campaign);

        eventPublisher.publishEvent(new CampaignCompletedEvent(campaign.getId(), campaign.getBookingId(), LocalDateTime.now()));

        Map<String, String> params = new LinkedHashMap<>();
        params.put("Campagne", campaign.getName());
        notifyAdvertiser(campaign, "CAMPAIGN_COMPLETED", params);
    }

    public List<Campaign> getCampaignsByStatus(CampaignStatus status) {
        return campaignRepository.findByStatus(status);
    }

    private void notifyAdvertiser(Campaign campaign, String templateCode, Map<String, String> params) {
        Optional<UUID> recipientUserId = advertiserFacade.findUserIdByAdvertiserId(campaign.getAdvertiserId());
        Optional<String> recipientEmail = advertiserFacade.findContactEmailByAdvertiserId(campaign.getAdvertiserId());
        if (recipientUserId.isEmpty() || recipientEmail.isEmpty()) {
            log.warn("Campagne {} : annonceur introuvable ({}), notification {} non envoyée", campaign.getId(), campaign.getAdvertiserId(), templateCode);
            return;
        }
        notificationFacade.sendNotification(recipientUserId.get(), recipientEmail.get(), templateCode, "EMAIL", params);
    }

    public Campaign getCampaignById(UUID id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new CampaignNotFoundException("Campagne non trouvée avec l'id : " + id));
    }

    public List<Campaign> getCampaignsByAdvertiser(UUID advertiserId) {
        return campaignRepository.findByAdvertiserId(advertiserId);
    }

    public List<Campaign> getCampaignsByBooking(UUID bookingId) {
        return campaignRepository.findByBookingId(bookingId);
    }

    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }
}
