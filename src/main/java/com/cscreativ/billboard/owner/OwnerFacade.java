package com.cscreativ.billboard.owner;

import com.cscreativ.billboard.owner.domain.BillboardOwner;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface OwnerFacade {
    Optional<BillboardOwner> findOwnerById(UUID ownerId);
    Optional<UUID> findOwnerIdByUserId(UUID userId);
    boolean isOwnerActive(UUID ownerId);
    Optional<UUID> findUserIdByOwnerId(UUID ownerId);
    Optional<String> findCompanyNameByOwnerId(UUID ownerId);
    Optional<String> findContactEmailByOwnerId(UUID ownerId);

    /**
     * BigDecimal brut (pas BillboardOwner, type interne au module) : utilisé par
     * booking.RevenueSplitListener pour répartir un paiement entre propriétaire et plateforme
     * sans dépendre d'un type non exposé par ce module.
     */
    Optional<BigDecimal> findRevenueShareRateByOwnerId(UUID ownerId);
}
