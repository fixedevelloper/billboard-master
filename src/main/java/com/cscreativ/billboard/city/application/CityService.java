package com.cscreativ.billboard.city.application;

import com.cscreativ.billboard.city.domain.City;
import com.cscreativ.billboard.city.domain.exception.CityNotFoundException;
import com.cscreativ.billboard.city.domain.repository.CityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional
    public City createCity(String name, String country, double latitude, double longitude) {
        if (cityRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new IllegalStateException("Cette ville est déjà enregistrée : " + name);
        }
        return cityRepository.save(City.create(name, country, latitude, longitude));
    }

    public City getCityById(UUID id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException("Ville non trouvée avec l'id : " + id));
    }

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public List<City> searchCities(String query) {
        if (query == null || query.isBlank()) {
            return cityRepository.findAll();
        }
        return cityRepository.search(query.trim());
    }

    @Transactional
    public City updateCity(UUID id, String name, String country, double latitude, double longitude) {
        City existing = getCityById(id);
        Optional<City> sameName = cityRepository.findByNameIgnoreCase(name);
        if (sameName.isPresent() && !sameName.get().getId().equals(id)) {
            throw new IllegalStateException("Cette ville est déjà enregistrée : " + name);
        }
        City updated = new City(id, name, country, latitude, longitude, existing.getCreatedAt());
        return cityRepository.save(updated);
    }

    @Transactional
    public void deleteCity(UUID id) {
        getCityById(id);
        cityRepository.deleteById(id);
    }
}
