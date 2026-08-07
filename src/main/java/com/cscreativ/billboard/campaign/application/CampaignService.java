package com.cscreativ.billboard.campaign.application;

import com.cscreativ.billboard.campaign.domain.Campaign;
import com.cscreativ.billboard.campaign.domain.exception.CampaignNotFoundException;
import com.cscreativ.billboard.campaign.domain.repository.CampaignRepository;
import com.cscreativ.billboard.campaign.domain.valueobject.MediaAsset;
import com.cscreativ.billboard.campaign.events.CampaignApprovedEvent;
import com.cscreativ.billboard.campaign.events.CampaignRejectedEvent;
import com.cscreativ.billboard.campaign.events.CampaignSubmittedEvent;
import com.cscreativ.billboard.payment.PaymentFacade;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PaymentFacade paymentFacade;

    public CampaignService(CampaignRepository campaignRepository, ApplicationEventPublisher eventPublisher,
                            PaymentFacade paymentFacade) {
        this.campaignRepository = campaignRepository;
        this.eventPublisher = eventPublisher;
        this.paymentFacade = paymentFacade;
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
    }

    @Transactional
    public void rejectCampaign(UUID campaignId, String reason) {
        Campaign campaign = getCampaignById(campaignId);
        campaign.reject(reason);
        campaignRepository.save(campaign);

        eventPublisher.publishEvent(new CampaignRejectedEvent(campaign.getId(), reason, LocalDateTime.now()));
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
