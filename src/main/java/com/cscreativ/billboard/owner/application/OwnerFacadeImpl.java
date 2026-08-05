package com.cscreativ.billboard.owner.application;

import com.cscreativ.billboard.owner.OwnerFacade;
import com.cscreativ.billboard.owner.domain.BillboardOwner;
import com.cscreativ.billboard.owner.domain.OwnerStatus;
import com.cscreativ.billboard.owner.domain.repository.BillboardOwnerRepository;
import org.springframework.stereotype.Component;

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
    public boolean isOwnerActive(UUID ownerId) {
        return ownerRepository.findById(ownerId)
                .map(owner -> owner.getStatus() == OwnerStatus.ACTIVE)
                .orElse(false);
    }
}
