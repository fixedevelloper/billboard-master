package com.cscreativ.billboard.billboard.application;

import com.cscreativ.billboard.billboard.BillboardFacade;
import com.cscreativ.billboard.billboard.domain.Billboard;
import com.cscreativ.billboard.billboard.domain.BillboardStatus;
import com.cscreativ.billboard.billboard.domain.repository.BillboardRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class BillboardFacadeImpl implements BillboardFacade {

    private final BillboardRepository billboardRepository;

    public BillboardFacadeImpl(BillboardRepository billboardRepository) {
        this.billboardRepository = billboardRepository;
    }

    @Override
    public Optional<Billboard> findBillboardById(UUID billboardId) {
        return billboardRepository.findById(billboardId);
    }

    @Override
    public boolean isAvailable(UUID billboardId) {
        return billboardRepository.findById(billboardId)
                .map(billboard -> billboard.getStatus() == BillboardStatus.AVAILABLE)
                .orElse(false);
    }

    @Override
    public Optional<UUID> findOwnerIdByBillboard(UUID billboardId) {
        return billboardRepository.findById(billboardId).map(Billboard::getOwnerId);
    }

    @Override
    public Optional<String> findTitleByBillboardId(UUID billboardId) {
        return billboardRepository.findById(billboardId).map(Billboard::getTitle);
    }
}
