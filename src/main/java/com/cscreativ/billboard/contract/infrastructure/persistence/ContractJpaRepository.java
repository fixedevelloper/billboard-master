package com.cscreativ.billboard.contract.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractJpaRepository extends JpaRepository<ContractEntity, UUID> {
    Optional<ContractEntity> findByBookingId(UUID bookingId);
    List<ContractEntity> findByOwnerId(UUID ownerId);
    List<ContractEntity> findByAdvertiserId(UUID advertiserId);
}
