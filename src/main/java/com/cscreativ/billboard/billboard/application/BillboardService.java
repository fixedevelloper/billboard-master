package com.cscreativ.billboard.billboard.application;

import com.cscreativ.billboard.billboard.domain.Billboard;
import com.cscreativ.billboard.billboard.domain.BillboardImage;
import com.cscreativ.billboard.billboard.domain.BillboardType;
import com.cscreativ.billboard.billboard.domain.exception.BillboardNotFoundException;
import com.cscreativ.billboard.billboard.domain.repository.BillboardImageRepository;
import com.cscreativ.billboard.billboard.domain.repository.BillboardRepository;
import com.cscreativ.billboard.billboard.domain.valueobject.Dimensions;
import com.cscreativ.billboard.billboard.domain.valueobject.Location;
import com.cscreativ.billboard.billboard.domain.valueobject.Pricing;
import com.cscreativ.billboard.billboard.events.BillboardCreatedEvent;
import com.cscreativ.billboard.city.CityFacade;
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
    private final BillboardImageRepository billboardImageRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CityFacade cityFacade;

    public BillboardService(BillboardRepository billboardRepository,
                             BillboardImageRepository billboardImageRepository,
                             ApplicationEventPublisher eventPublisher,
                             CityFacade cityFacade) {
        this.billboardRepository = billboardRepository;
        this.billboardImageRepository = billboardImageRepository;
        this.eventPublisher = eventPublisher;
        this.cityFacade = cityFacade;
    }

    @Transactional
    public Billboard createBillboard(String title, String description, BillboardType type,
                                      String address, String city, double lat, double lng,
                                      double width, double height, BigDecimal dailyRate, String currency, UUID ownerId) {
        requireKnownCity(city);
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

    public List<Billboard> getAllBillboards() {
        return billboardRepository.findAll();
    }

    public List<Billboard> getBillboardsByOwner(UUID ownerId) {
        return billboardRepository.findAllByOwnerId(ownerId);
    }

    @Transactional
    public Billboard updateBillboard(UUID id, String title, String description, BillboardType type,
                                      String address, String city, double lat, double lng,
                                      double width, double height, BigDecimal dailyRate, String currency) {
        requireKnownCity(city);
        Billboard billboard = getBillboardById(id);

        Location location = new Location(address, city, lat, lng);
        Dimensions dimensions = new Dimensions(width, height);
        Pricing pricing = new Pricing(dailyRate, currency);

        billboard.updateDetails(title, description, type, location, dimensions, pricing);
        return billboardRepository.save(billboard);
    }

    @Transactional
    public void deleteBillboard(UUID id) {
        getBillboardById(id);
        billboardImageRepository.findByBillboardId(id).forEach(image -> billboardImageRepository.deleteById(image.getId()));
        billboardRepository.deleteById(id);
    }

    @Transactional
    public BillboardImage addImage(UUID billboardId, UUID fileId) {
        getBillboardById(billboardId);
        BillboardImage image = BillboardImage.create(billboardId, fileId);
        return billboardImageRepository.save(image);
    }

    public List<BillboardImage> getImages(UUID billboardId) {
        return billboardImageRepository.findByBillboardId(billboardId);
    }

    @Transactional
    public void removeImage(UUID imageId) {
        billboardImageRepository.deleteById(imageId);
    }

    private void requireKnownCity(String city) {
        if (!cityFacade.existsByName(city)) {
            throw new IllegalArgumentException("Ville inconnue : " + city + " (enregistrez-la d'abord via /api/v1/cities)");
        }
    }
}
