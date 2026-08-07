package com.cscreativ.billboard.booking.infrastructure.persistence;

import com.cscreativ.billboard.booking.domain.Booking;
import com.cscreativ.billboard.booking.domain.BookingStatus;
import com.cscreativ.billboard.booking.domain.repository.BookingRepository;
import com.cscreativ.billboard.booking.domain.valueobject.DateRange;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BookingPersistenceAdapter implements BookingRepository {

    private final BookingJpaRepository jpaRepository;

    public BookingPersistenceAdapter(BookingJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Booking save(Booking booking) {
        BookingEntity entity = toEntity(booking);
        BookingEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Booking> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Booking> findByBillboardId(UUID billboardId) {
        return jpaRepository.findByBillboardId(billboardId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByAdvertiserId(UUID advertiserId) {
        return jpaRepository.findByAdvertiserId(advertiserId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByStatusIn(Collection<BookingStatus> statuses) {
        return jpaRepository.findByStatusIn(statuses).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsOverlappingBooking(UUID billboardId, DateRange period) {
        List<BookingStatus> activeStatuses = List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);
        return jpaRepository.existsOverlapping(billboardId, period.getStartDate(), period.getEndDate(), activeStatuses);
    }

    @Override
    public List<Booking> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private BookingEntity toEntity(Booking domain) {
        BookingEntity entity = new BookingEntity();
        entity.setId(domain.getId());
        entity.setBillboardId(domain.getBillboardId());
        entity.setAdvertiserId(domain.getAdvertiserId());
        entity.setStartDate(domain.getPeriod().getStartDate());
        entity.setEndDate(domain.getPeriod().getEndDate());
        entity.setTotalPrice(domain.getTotalPrice());
        entity.setCurrency(domain.getCurrency());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private Booking toDomain(BookingEntity entity) {
        return new Booking(
                entity.getId(),
                entity.getBillboardId(),
                entity.getAdvertiserId(),
                new DateRange(entity.getStartDate(), entity.getEndDate()),
                entity.getTotalPrice(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
