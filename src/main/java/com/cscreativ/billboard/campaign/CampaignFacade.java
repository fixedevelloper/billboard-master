package com.cscreativ.billboard.campaign;

import com.cscreativ.billboard.campaign.domain.Campaign;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampaignFacade {
    Optional<Campaign> findCampaignById(UUID campaignId);
    boolean isApproved(UUID campaignId);
    List<UUID> findCampaignIdsByBooking(UUID bookingId);
    Optional<UUID> findAdvertiserIdByCampaign(UUID campaignId);
    Optional<UUID> findBookingIdByCampaign(UUID campaignId);

    /**
     * Appelée par le module installation à la fin de la pose physique du visuel (voir
     * InstallationService.completeTask) : c'est ce module qui dépend déjà de campaign
     * (CampaignFacade), pas l'inverse, pour ne pas créer de cycle Spring Modulith. Ne fait rien
     * (log) si la campagne n'est pas dans un état permettant l'activation.
     */
    void activateCampaign(UUID campaignId);
}
