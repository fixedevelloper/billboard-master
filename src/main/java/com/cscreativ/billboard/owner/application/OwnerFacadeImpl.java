package com.cscreativ.billboard.owner.application;

import com.cscreativ.billboard.owner.OwnerFacade;
import com.cscreativ.billboard.owner.domain.BillboardOwner;
import com.cscreativ.billboard.owner.domain.OwnerStatus;
import com.cscreativ.billboard.owner.domain.repository.BillboardOwnerRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class OwnerFacadeImpl implements OwnerFacade {

    private final BillboardOwnerRepository ownerRepository;

    public OwnerFacadeImpl(BillboardOwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public Optional<BillboardOwner> findOwnerById(UUID ownerId) {
        return ownerRepository.findById(ownerId);
    }

    @Override
    public Optional<UUID> findOwnerIdByUserId(UUID userId) {
        return ownerRepository.findByUserId(userId).map(BillboardOwner::getId);
    }

    @Override
    public boolean isOwnerActive(UUID ownerId) {
        return ownerRepository.findById(ownerId)
                .map(owner -> owner.getStatus() == OwnerStatus.ACTIVE)
                .orElse(false);
    }

    @Override
    public Optional<UUID> findUserIdByOwnerId(UUID ownerId) {
        return ownerRepository.findById(ownerId).map(BillboardOwner::getUserId);
    }

    @Override
    public Optional<String> findCompanyNameByOwnerId(UUID ownerId) {
        return ownerRepository.findById(ownerId).map(owner -> owner.getOwnerDetails().getCompanyName());
    }

    @Override
    public Optional<String> findContactEmailByOwnerId(UUID ownerId) {
        return ownerRepository.findById(ownerId).map(owner -> owner.getOwnerDetails().getContactEmail());
    }

    @Override
    public Optional<BigDecimal> findRevenueShareRateByOwnerId(UUID ownerId) {
        return ownerRepository.findById(ownerId).map(BillboardOwner::getRevenueShareRate);
    }
}
