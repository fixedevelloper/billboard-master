package com.cscreativ.billboard.city.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class City {
    private final UUID id;
    private final String name;
    private final String country;
    private final double latitude;
    private final double longitude;
    private final LocalDateTime createdAt;

    public City(UUID id, String name, String country, double latitude, double longitude, LocalDateTime createdAt) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom de la ville ne peut pas être vide");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("La latitude doit être comprise entre -90 et 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("La longitude doit être comprise entre -180 et 180");
        }
        this.id = id;
        this.name = name.trim();
        this.country = country == null || country.isBlank() ? null : country.trim();
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    public static City create(String name, String country, double latitude, double longitude) {
        return new City(UUID.randomUUID(), name, country, latitude, longitude, LocalDateTime.now());
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getCountry() { return country; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
