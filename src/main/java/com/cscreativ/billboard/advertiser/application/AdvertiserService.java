package com.cscreativ.billboard.advertiser.application;

import com.cscreativ.billboard.advertiser.domain.Advertiser;
import com.cscreativ.billboard.advertiser.domain.exception.AdvertiserAlreadyExistsException;
import com.cscreativ.billboard.advertiser.domain.exception.AdvertiserNotFoundException;
import com.cscreativ.billboard.advertiser.domain.repository.AdvertiserRepository;
import com.cscreativ.billboard.advertiser.domain.valueobject.CompanyName;
import com.cscreativ.billboard.advertiser.domain.valueobject.TaxNumber;
import com.cscreativ.billboard.advertiser.events.AdvertiserRegisteredEvent;
import com.cscreativ.billboard.advertiser.events.AdvertiserVerifiedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AdvertiserService {

    private final AdvertiserRepository advertiserRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AdvertiserService(AdvertiserRepository advertiserRepository, ApplicationEventPublisher eventPublisher) {
        this.advertiserRepository = advertiserRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Advertiser registerAdvertiser(UUID userId, String companyNameStr, String taxNumberStr, String contactEmail, String contactPhone) {
        TaxNumber taxNumber = new TaxNumber(taxNumberStr);
        if (advertiserRepository.existsByTaxNumber(taxNumber)) {
            throw new AdvertiserAlreadyExistsException("Un compte annonceur existe déjà avec le numéro fiscal : " + taxNumberStr);
        }

        CompanyName companyName = new CompanyName(companyNameStr);
        Advertiser advertiser = Advertiser.create(userId, companyName, taxNumber, contactEmail, contactPhone);
        Advertiser saved = advertiserRepository.save(advertiser);

        eventPublisher.publishEvent(new AdvertiserRegisteredEvent(saved.getId(), saved.getUserId(), saved.getCompanyName().getValue(), LocalDateTime.now()));
        return saved;
    }

    @Transactional
    public void verifyAdvertiser(UUID advertiserId) {
        Advertiser advertiser = advertiserRepository.findById(advertiserId)
                .orElseThrow(() -> new AdvertiserNotFoundException("Annonceur non trouvé avec l'id : " + advertiserId));

        advertiser.verify();
        advertiserRepository.save(advertiser);

        eventPublisher.publishEvent(new AdvertiserVerifiedEvent(advertiser.getId(), LocalDateTime.now()));
    }

    public Advertiser getAdvertiserById(UUID id) {
        return advertiserRepository.findById(id)
                .orElseThrow(() -> new AdvertiserNotFoundException("Annonceur non trouvé avec l'id : " + id));
    }
}
