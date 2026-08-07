package com.cscreativ.billboard.advertiser.application;

import com.cscreativ.billboard.advertiser.AdvertiserFacade;
import com.cscreativ.billboard.advertiser.domain.Advertiser;
import com.cscreativ.billboard.advertiser.domain.AdvertiserStatus;
import com.cscreativ.billboard.advertiser.domain.repository.AdvertiserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AdvertiserFacadeImpl implements AdvertiserFacade {

    private final AdvertiserRepository advertiserRepository;

    public AdvertiserFacadeImpl(AdvertiserRepository advertiserRepository) {
        this.advertiserRepository = advertiserRepository;
    }

    @Override
    public Optional<Advertiser> findAdvertiserById(UUID advertiserId) {
        return advertiserRepository.findById(advertiserId);
    }

    @Override
    public Optional<UUID> findAdvertiserIdByUserId(UUID userId) {
        return advertiserRepository.findByUserId(userId).map(Advertiser::getId);
    }

    @Override
    public boolean isVerified(UUID advertiserId) {
        return advertiserRepository.findById(advertiserId)
                .map(advertiser -> advertiser.getStatus() == AdvertiserStatus.VERIFIED)
                .orElse(false);
    }
}
