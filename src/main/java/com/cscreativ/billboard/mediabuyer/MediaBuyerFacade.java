package com.cscreativ.billboard.mediabuyer;

import com.cscreativ.billboard.mediabuyer.domain.MediaBuyer;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface MediaBuyerFacade {
    Optional<MediaBuyer> findBuyerById(UUID buyerId);
    Optional<UUID> findBuyerIdByUserId(UUID userId);
    boolean hasSufficientCredit(UUID buyerId, BigDecimal requiredAmount);
    Optional<UUID> findUserIdByBuyerId(UUID buyerId);
    Optional<String> findCompanyNameByBuyerId(UUID buyerId);
    Optional<String> findContactEmailByBuyerId(UUID buyerId);
}
