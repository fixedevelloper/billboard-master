package com.cscreativ.billboard.city.api.request;

public record CreateCityRequest(
        String name,
        String country,
        double latitude,
        double longitude
) {}
