package com.cscreativ.billboard.contract;

import com.cscreativ.billboard.contract.domain.Contract;

import java.util.Optional;
import java.util.UUID;

public interface ContractFacade {
    Optional<Contract> findContractById(UUID contractId);
    boolean isSignedByAllParties(UUID contractId);
    boolean isSignedForBooking(UUID bookingId);
}
