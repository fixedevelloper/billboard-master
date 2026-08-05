package com.cscreativ.billboard.booking.infrastructure.persistence;

import com.cscreativ.billboard.booking.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, UUID> {
    List<BookingEntity> findByBillboardId(UUID billboardId);
    List<BookingEntity> findByAdvertiserId(UUID advertiserId);

    @Query("SELECT COUNT(b) > 0 FROM BookingEntity b WHERE b.billboardId = :billboardId " +
           "AND b.status IN (:activeStatuses) " +
           "AND NOT (b.endDate < :startDate OR b.startDate > :endDate)")
    boolean existsOverlapping(@Param("billboardId") UUID billboardId,
                             @Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate,
                             @Param("activeStatuses") List<BookingStatus> activeStatuses);
}
