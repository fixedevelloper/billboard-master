package com.cscreativ.billboard.billboard.domain.valueobject;

import java.util.Objects;

public class Location {
    private final String address;
    private final String city;
    private final double latitude;
    private final double longitude;

    public Location(String address, String city, double latitude, double longitude) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("L'adresse ne peut pas être vide");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("La ville ne peut pas être vide");
        }
        this.address = address.trim();
        this.city = city.trim();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() { return address; }
    public String getCity() { return city; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.latitude, latitude) == 0 &&
               Double.compare(location.longitude, longitude) == 0 &&
               Objects.equals(address, location.address) &&
               Objects.equals(city, location.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, city, latitude, longitude);
    }
}
