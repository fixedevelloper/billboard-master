package com.cscreativ.billboard.advertiser;

import com.cscreativ.billboard.advertiser.domain.Advertiser;

import java.util.Optional;
import java.util.UUID;

public interface AdvertiserFacade {
    Optional<Advertiser> findAdvertiserById(UUID advertiserId);
    boolean isVerified(UUID advertiserId);
}
