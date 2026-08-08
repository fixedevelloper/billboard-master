package com.cscreativ.billboard.billboard;

import com.cscreativ.billboard.billboard.domain.Billboard;

import java.util.Optional;
import java.util.UUID;

public interface BillboardFacade {
    Optional<Billboard> findBillboardById(UUID billboardId);
    boolean isAvailable(UUID billboardId);
    Optional<UUID> findOwnerIdByBillboard(UUID billboardId);
    Optional<String> findTitleByBillboardId(UUID billboardId);
}
