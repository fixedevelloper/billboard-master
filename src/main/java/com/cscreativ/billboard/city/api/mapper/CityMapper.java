package com.cscreativ.billboard.city.api.mapper;

import com.cscreativ.billboard.city.api.response.CityResponse;
import com.cscreativ.billboard.city.domain.City;
import org.springframework.stereotype.Component;

@Component
public class CityMapper {

    public CityResponse toResponse(City city) {
        return new CityResponse(city.getId(), city.getName(), city.getCountry(), city.getLatitude(), city.getLongitude());
    }
}
