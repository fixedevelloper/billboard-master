package com.cscreativ.billboard.billboard.application;

import com.cscreativ.billboard.billboard.domain.Billboard;
import com.cscreativ.billboard.billboard.domain.BillboardType;
import com.cscreativ.billboard.billboard.domain.exception.BillboardNotFoundException;
import com.cscreativ.billboard.billboard.domain.repository.BillboardRepository;
import com.cscreativ.billboard.billboard.domain.valueobject.Dimensions;
import com.cscreativ.billboard.billboard.domain.valueobject.Location;
import com.cscreativ.billboard.billboard.domain.valueobject.Pricing;
import com.cscreativ.billboard.billboard.events.BillboardCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BillboardService {

    private final BillboardRepository billboardRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BillboardService(BillboardRepository billboardRepository, ApplicationEventPublisher eventPublisher) {
        this.billboardRepository = billboardRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Billboard createBillboard(String title, String description, BillboardType type,
                                      String address, String city, double lat, double lng,
                                      double width, double height, BigDecimal dailyRate, String currency, UUID ownerId) {
        Location location = new Location(address, city, lat, lng);
        Dimensions dimensions = new Dimensions(width, height);
        Pricing pricing = new Pricing(dailyRate, currency);

        Billboard billboard = Billboard.create(title, description, type, location, dimensions, pricing, ownerId);
        Billboard saved = billboardRepository.save(billboard);

        eventPublisher.publishEvent(new BillboardCreatedEvent(saved.getId(), saved.getOwnerId(), LocalDateTime.now()));
        return saved;
    }

    public Billboard getBillboardById(UUID id) {
        return billboardRepository.findById(id)
                .orElseThrow(() -> new BillboardNotFoundException("Panneau publicitaire non trouvé avec l'id : " + id));
    }

    public List<Billboard> getBillboardsByCity(String city) {
        return billboardRepository.findAllByCity(city);
    }
}
