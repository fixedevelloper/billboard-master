package com.cscreativ.billboard.city.application;

import com.cscreativ.billboard.city.CityFacade;
import com.cscreativ.billboard.city.domain.repository.CityRepository;
import org.springframework.stereotype.Component;

@Component
public class CityFacadeImpl implements CityFacade {

    private final CityRepository cityRepository;

    public CityFacadeImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public boolean existsByName(String name) {
        return name != null && cityRepository.findByNameIgnoreCase(name).isPresent();
    }
}
