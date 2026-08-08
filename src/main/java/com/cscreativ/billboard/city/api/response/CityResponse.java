package com.cscreativ.billboard.city.api.response;

import java.util.UUID;

public record CityResponse(
        UUID id,
        String name,
        String country,
        double latitude,
        double longitude
) {}
