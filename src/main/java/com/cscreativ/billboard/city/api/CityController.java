package com.cscreativ.billboard.city.api;

import com.cscreativ.billboard.city.api.mapper.CityMapper;
import com.cscreativ.billboard.city.api.request.CreateCityRequest;
import com.cscreativ.billboard.city.api.response.CityResponse;
import com.cscreativ.billboard.city.application.CityService;
import com.cscreativ.billboard.city.domain.City;
import com.cscreativ.billboard.shared.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/cities")
public class CityController {

    private final CityService cityService;
    private final CityMapper cityMapper;

    public CityController(CityService cityService, CityMapper cityMapper) {
        this.cityService = cityService;
        this.cityMapper = cityMapper;
    }

    @PostMapping
    public ResponseEntity<CityResponse> createCity(@RequestBody CreateCityRequest request,
                                                    @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        City city = cityService.createCity(request.name(), request.country(), request.latitude(), request.longitude());
        return ResponseEntity.ok(cityMapper.toResponse(city));
    }

    @GetMapping
    public ResponseEntity<List<CityResponse>> getCities(@RequestParam(required = false) String query) {
        List<City> cities = cityService.searchCities(query);
        return ResponseEntity.ok(cities.stream().map(cityMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityResponse> getCityById(@PathVariable UUID id) {
        return ResponseEntity.ok(cityMapper.toResponse(cityService.getCityById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CityResponse> updateCity(@PathVariable UUID id, @RequestBody CreateCityRequest request,
                                                    @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        City city = cityService.updateCity(id, request.name(), request.country(), request.latitude(), request.longitude());
        return ResponseEntity.ok(cityMapper.toResponse(city));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }
}
