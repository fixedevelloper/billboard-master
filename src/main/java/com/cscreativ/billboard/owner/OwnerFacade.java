package com.cscreativ.billboard.owner;

import com.cscreativ.billboard.owner.domain.BillboardOwner;

import java.util.Optional;
import java.util.UUID;

public interface OwnerFacade {
    Optional<BillboardOwner> findOwnerById(UUID ownerId);
    boolean isOwnerActive(UUID ownerId);
}
