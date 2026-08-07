package com.cscreativ.billboard.advertiser;

import com.cscreativ.billboard.advertiser.domain.Advertiser;

import java.util.Optional;
import java.util.UUID;

public interface AdvertiserFacade {
    Optional<Advertiser> findAdvertiserById(UUID advertiserId);
    // Retourne l'id (pas l'entité Advertiser) : le type domain.Advertiser n'est pas exposé
    // hors du module, même en retour d'une méthode de Facade (vérifié par ApplicationModules.verify()).
    Optional<UUID> findAdvertiserIdByUserId(UUID userId);
    boolean isVerified(UUID advertiserId);
}
