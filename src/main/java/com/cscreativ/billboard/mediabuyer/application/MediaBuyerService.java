package com.cscreativ.billboard.mediabuyer.application;

import com.cscreativ.billboard.mediabuyer.domain.MediaBuyer;
import com.cscreativ.billboard.mediabuyer.domain.exception.MediaBuyerNotFoundException;
import com.cscreativ.billboard.mediabuyer.domain.repository.MediaBuyerRepository;
import com.cscreativ.billboard.mediabuyer.domain.valueobject.CompanyDetails;
import com.cscreativ.billboard.mediabuyer.events.CreditLimitUpdatedEvent;
import com.cscreativ.billboard.mediabuyer.events.MediaBuyerRegisteredEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MediaBuyerService {

    private final MediaBuyerRepository mediaBuyerRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MediaBuyerService(MediaBuyerRepository mediaBuyerRepository, ApplicationEventPublisher eventPublisher) {
        this.mediaBuyerRepository = mediaBuyerRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public MediaBuyer registerBuyer(UUID userId, String companyName, String taxId, String email, String phone, BigDecimal creditLimit) {
        CompanyDetails details = new CompanyDetails(companyName, taxId, email, phone);
        MediaBuyer buyer = MediaBuyer.create(userId, details, creditLimit);
        MediaBuyer saved = mediaBuyerRepository.save(buyer);

        eventPublisher.publishEvent(new MediaBuyerRegisteredEvent(saved.getId(), saved.getUserId(), saved.getCompanyDetails().getCompanyName(), LocalDateTime.now()));
        return saved;
    }

    @Transactional
    public void activateBuyer(UUID buyerId) {
        MediaBuyer buyer = getBuyerById(buyerId);
        buyer.activate();
        mediaBuyerRepository.save(buyer);
    }

    @Transactional
    public void updateCreditLimit(UUID buyerId, BigDecimal newLimit) {
        MediaBuyer buyer = getBuyerById(buyerId);
        buyer.updateCreditLimit(newLimit);
        MediaBuyer saved = mediaBuyerRepository.save(buyer);

        eventPublisher.publishEvent(new CreditLimitUpdatedEvent(saved.getId(), saved.getCreditLimit(), LocalDateTime.now()));
    }

    @Transactional
    public void recordSpending(UUID buyerId, BigDecimal amount) {
        MediaBuyer buyer = getBuyerById(buyerId);
        buyer.recordSpending(amount);
        mediaBuyerRepository.save(buyer);
    }

    public MediaBuyer getBuyerById(UUID id) {
        return mediaBuyerRepository.findById(id)
                .orElseThrow(() -> new MediaBuyerNotFoundException("Acheteur média non trouvé avec l'id : " + id));
    }

    public MediaBuyer getBuyerByUserId(UUID userId) {
        return mediaBuyerRepository.findByUserId(userId)
                .orElseThrow(() -> new MediaBuyerNotFoundException("Acheteur média non trouvé pour l'utilisateur : " + userId));
    }

    public List<MediaBuyer> getAllBuyers() {
        return mediaBuyerRepository.findAll();
    }
}
