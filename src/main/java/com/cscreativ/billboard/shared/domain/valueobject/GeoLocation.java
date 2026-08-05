package com.cscreativ.billboard.shared.domain.valueobject;

import java.util.Objects;

public class GeoLocation {
    private final double latitude;
    private final double longitude;

    public GeoLocation(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("La latitude doit être comprise entre -90 et 90 degrés");
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("La longitude doit être comprise entre -180 et 180 degrés");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoLocation that = (GeoLocation) o;
        return Double.compare(that.latitude, latitude) == 0 && Double.compare(that.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
