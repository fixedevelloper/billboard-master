package com.cscreativ.billboard.owner.domain.repository;

import com.cscreativ.billboard.owner.domain.BillboardOwner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillboardOwnerRepository {
    BillboardOwner save(BillboardOwner owner);
    Optional<BillboardOwner> findById(UUID id);
    Optional<BillboardOwner> findByUserId(UUID userId);
    List<BillboardOwner> findAll();
}
