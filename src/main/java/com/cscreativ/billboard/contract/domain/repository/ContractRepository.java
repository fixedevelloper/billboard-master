package com.cscreativ.billboard.contract.domain.repository;

import com.cscreativ.billboard.contract.domain.Contract;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractRepository {
    Contract save(Contract contract);
    Optional<Contract> findById(UUID id);
    Optional<Contract> findByBookingId(UUID bookingId);
    List<Contract> findByOwnerId(UUID ownerId);
    List<Contract> findByAdvertiserId(UUID advertiserId);
}
