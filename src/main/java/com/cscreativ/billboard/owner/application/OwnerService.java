package com.cscreativ.billboard.owner.application;

import com.cscreativ.billboard.owner.domain.BillboardOwner;
import com.cscreativ.billboard.owner.domain.exception.OwnerNotFoundException;
import com.cscreativ.billboard.owner.domain.repository.BillboardOwnerRepository;
import com.cscreativ.billboard.owner.domain.valueobject.OwnerDetails;
import com.cscreativ.billboard.owner.events.OwnerActivatedEvent;
import com.cscreativ.billboard.owner.events.OwnerRegisteredEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OwnerService {

    private final BillboardOwnerRepository ownerRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OwnerService(BillboardOwnerRepository ownerRepository, ApplicationEventPublisher eventPublisher) {
        this.ownerRepository = ownerRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public BillboardOwner registerOwner(UUID userId, String companyName, String registrationNumber, String email, String phone, BigDecimal revenueShareRate) {
        OwnerDetails details = new OwnerDetails(companyName, registrationNumber, email, phone);
        BillboardOwner owner = BillboardOwner.create(userId, details, revenueShareRate);
        BillboardOwner saved = ownerRepository.save(owner);

        eventPublisher.publishEvent(new OwnerRegisteredEvent(saved.getId(), saved.getUserId(), saved.getOwnerDetails().getCompanyName(), LocalDateTime.now()));
        return saved;
    }

    @Transactional
    public void activateOwner(UUID ownerId) {
        BillboardOwner owner = getOwnerById(ownerId);
        owner.activate();
        ownerRepository.save(owner);

        eventPublisher.publishEvent(new OwnerActivatedEvent(owner.getId(), LocalDateTime.now()));
    }

    @Transactional
    public void updateRevenueShareRate(UUID ownerId, BigDecimal newRate) {
        BillboardOwner owner = getOwnerById(ownerId);
        owner.updateRevenueShareRate(newRate);
        ownerRepository.save(owner);
    }

    public BillboardOwner getOwnerById(UUID id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new OwnerNotFoundException("Régisseur non trouvé avec l'id : " + id));
    }

    public BillboardOwner getOwnerByUserId(UUID userId) {
        return ownerRepository.findByUserId(userId)
                .orElseThrow(() -> new OwnerNotFoundException("Régisseur non trouvé pour l'utilisateur : " + userId));
    }

    public List<BillboardOwner> getAllOwners() {
        return ownerRepository.findAll();
    }
}
