package com.cscreativ.billboard.contract.application;

import com.cscreativ.billboard.contract.ContractFacade;
import com.cscreativ.billboard.contract.domain.Contract;
import com.cscreativ.billboard.contract.domain.ContractStatus;
import com.cscreativ.billboard.contract.domain.repository.ContractRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ContractFacadeImpl implements ContractFacade {

    private final ContractRepository contractRepository;

    public ContractFacadeImpl(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    @Override
    public Optional<Contract> findContractById(UUID contractId) {
        return contractRepository.findById(contractId);
    }

    @Override
    public boolean isSignedByAllParties(UUID contractId) {
        return contractRepository.findById(contractId)
                .map(c -> c.getStatus() == ContractStatus.SIGNED)
                .orElse(false);
    }

    @Override
    public boolean isSignedForBooking(UUID bookingId) {
        return contractRepository.findByBookingId(bookingId)
                .map(c -> c.getStatus() == ContractStatus.SIGNED)
                .orElse(false);
    }

    @Override
    public Optional<UUID> findOwnerIdByBooking(UUID bookingId) {
        return contractRepository.findByBookingId(bookingId).map(Contract::getOwnerId);
    }

    @Override
    public Optional<UUID> findAdvertiserIdByBooking(UUID bookingId) {
        return contractRepository.findByBookingId(bookingId).map(Contract::getAdvertiserId);
    }
}
