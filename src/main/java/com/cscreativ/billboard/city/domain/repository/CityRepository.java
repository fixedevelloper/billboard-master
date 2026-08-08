package com.cscreativ.billboard.city.domain.repository;

import com.cscreativ.billboard.city.domain.City;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CityRepository {
    City save(City city);
    Optional<City> findById(UUID id);
    Optional<City> findByNameIgnoreCase(String name);
    List<City> findAll();
    List<City> search(String query);
    void deleteById(UUID id);
}
