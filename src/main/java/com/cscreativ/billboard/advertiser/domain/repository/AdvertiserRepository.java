package com.cscreativ.billboard.advertiser.domain.repository;

import com.cscreativ.billboard.advertiser.domain.Advertiser;
import com.cscreativ.billboard.advertiser.domain.valueobject.TaxNumber;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdvertiserRepository {
    Advertiser save(Advertiser advertiser);
    Optional<Advertiser> findById(UUID id);
    Optional<Advertiser> findByUserId(UUID userId);
    boolean existsByTaxNumber(TaxNumber taxNumber);
    List<Advertiser> findAll();
}
