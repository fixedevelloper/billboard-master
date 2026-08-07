package com.cscreativ.billboard.campaign.infrastructure.persistence;

import com.cscreativ.billboard.campaign.domain.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CampaignJpaRepository extends JpaRepository<CampaignEntity, UUID> {
    List<CampaignEntity> findByAdvertiserId(UUID advertiserId);
    List<CampaignEntity> findByStatus(CampaignStatus status);
    List<CampaignEntity> findByBookingId(UUID bookingId);
}
