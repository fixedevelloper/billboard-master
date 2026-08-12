package com.cscreativ.billboard.campaign.application;

import com.cscreativ.billboard.booking.BookingFacade;
import com.cscreativ.billboard.campaign.domain.Campaign;
import com.cscreativ.billboard.campaign.domain.CampaignStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Fait passer à COMPLETED les campagnes ACTIVE dont la période de réservation est terminée.
 * Même approche que BookingExpirationScheduler (booking) : un job quotidien plutôt qu'un
 * déclenchement événementiel, la fin de période étant une question de date et non d'action
 * utilisateur.
 */
@Component
public class CampaignCompletionScheduler {

    private static final Logger log = LoggerFactory.getLogger(CampaignCompletionScheduler.class);

    private final CampaignService campaignService;
    private final BookingFacade bookingFacade;

    public CampaignCompletionScheduler(CampaignService campaignService, BookingFacade bookingFacade) {
        this.campaignService = campaignService;
        this.bookingFacade = bookingFacade;
    }

    @Scheduled(cron = "0 30 3 * * *")
    @Transactional
    public void completeFinishedCampaigns() {
        List<Campaign> activeCampaigns = campaignService.getCampaignsByStatus(CampaignStatus.ACTIVE);
        LocalDate today = LocalDate.now();

        int completedCount = 0;
        for (Campaign campaign : activeCampaigns) {
            LocalDate endDate = bookingFacade.findPeriodEndDateByBooking(campaign.getBookingId()).orElse(null);
            if (endDate == null) {
                log.warn("Campagne {} active : réservation {} introuvable, complétion ignorée", campaign.getId(), campaign.getBookingId());
                continue;
            }
            if (endDate.isBefore(today)) {
                campaignService.completeCampaign(campaign.getId());
                completedCount++;
            }
        }

        if (completedCount > 0) {
            log.info("{} campagne(s) marquée(s) COMPLETED (période de réservation écoulée)", completedCount);
        }
    }
}
