package com.cscreativ.billboard.mediabuyer.application;

import com.cscreativ.billboard.mediabuyer.MediaBuyerFacade;
import com.cscreativ.billboard.mediabuyer.domain.MediaBuyer;
import com.cscreativ.billboard.mediabuyer.domain.repository.MediaBuyerRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class MediaBuyerFacadeImpl implements MediaBuyerFacade {

    private final MediaBuyerRepository mediaBuyerRepository;

    public MediaBuyerFacadeImpl(MediaBuyerRepository mediaBuyerRepository) {
        this.mediaBuyerRepository = mediaBuyerRepository;
    }

    @Override
    public Optional<MediaBuyer> findBuyerById(UUID buyerId) {
        return mediaBuyerRepository.findById(buyerId);
    }

    @Override
    public Optional<UUID> findBuyerIdByUserId(UUID userId) {
        return mediaBuyerRepository.findByUserId(userId).map(MediaBuyer::getId);
    }

    @Override
    public boolean hasSufficientCredit(UUID buyerId, BigDecimal requiredAmount) {
        return mediaBuyerRepository.findById(buyerId)
                .map(buyer -> buyer.canSpend(requiredAmount))
                .orElse(false);
    }

    @Override
    public Optional<UUID> findUserIdByBuyerId(UUID buyerId) {
        return mediaBuyerRepository.findById(buyerId).map(MediaBuyer::getUserId);
    }

    @Override
    public Optional<String> findCompanyNameByBuyerId(UUID buyerId) {
        return mediaBuyerRepository.findById(buyerId).map(buyer -> buyer.getCompanyDetails().getCompanyName());
    }

    @Override
    public Optional<String> findContactEmailByBuyerId(UUID buyerId) {
        return mediaBuyerRepository.findById(buyerId).map(buyer -> buyer.getCompanyDetails().getContactEmail());
    }
}
