package com.cscreativ.billboard.booking.domain;

import com.cscreativ.billboard.booking.domain.valueobject.DateRange;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Booking {
    private UUID id;
    private UUID billboardId;
    private UUID advertiserId;
    private DateRange period;
    private BigDecimal totalPrice;
    private String currency;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Booking(UUID id, UUID billboardId, UUID advertiserId, DateRange period, BigDecimal totalPrice, String currency, BookingStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.billboardId = billboardId;
        this.advertiserId = advertiserId;
        this.period = period;
        this.totalPrice = totalPrice;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Booking create(UUID billboardId, UUID advertiserId, DateRange period, BigDecimal dailyRate, String currency) {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal total = dailyRate.multiply(BigDecimal.valueOf(period.getDurationInDays()));
        return new Booking(UUID.randomUUID(), billboardId, advertiserId, period, total, currency, BookingStatus.PENDING, now, now);
    }

    public void confirm() {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("Seule une réservation en attente peut être confirmée");
        }
        this.status = BookingStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Une réservation terminée ne peut pas être annulée");
        }
        this.status = BookingStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject() {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("Seule une réservation en attente peut être rejetée");
        }
        this.status = BookingStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getBillboardId() { return billboardId; }
    public UUID getAdvertiserId() { return advertiserId; }
    public DateRange getPeriod() { return period; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public String getCurrency() { return currency; }
    public BookingStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
