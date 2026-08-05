package com.cscreativ.billboard.contract.application;

import com.cscreativ.billboard.contract.domain.Contract;
import com.cscreativ.billboard.contract.domain.ContractStatus;
import com.cscreativ.billboard.contract.domain.exception.ContractNotFoundException;
import com.cscreativ.billboard.contract.domain.repository.ContractRepository;
import com.cscreativ.billboard.contract.events.ContractCreatedEvent;
import com.cscreativ.billboard.contract.events.ContractSignedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ContractService(ContractRepository contractRepository, ApplicationEventPublisher eventPublisher) {
        this.contractRepository = contractRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Contract createContract(UUID bookingId, UUID ownerId, UUID advertiserId, String termsAndConditions) {
        Contract contract = Contract.create(bookingId, ownerId, advertiserId, termsAndConditions);
        Contract saved = contractRepository.save(contract);

        eventPublisher.publishEvent(new ContractCreatedEvent(saved.getId(), saved.getBookingId(), LocalDateTime.now()));
        return saved;
    }

    @Transactional
    public void publishForSignature(UUID contractId) {
        Contract contract = getContractById(contractId);
        contract.publishForSignature();
        contractRepository.save(contract);
    }

    @Transactional
    public void signByOwner(UUID contractId, String signerName, String ipAddress) {
        Contract contract = getContractById(contractId);
        contract.signByOwner(signerName, ipAddress);
        Contract saved = contractRepository.save(contract);

        if (saved.getStatus() == ContractStatus.SIGNED) {
            eventPublisher.publishEvent(new ContractSignedEvent(saved.getId(), saved.getBookingId(), LocalDateTime.now()));
        }
    }

    @Transactional
    public void signByAdvertiser(UUID contractId, String signerName, String ipAddress) {
        Contract contract = getContractById(contractId);
        contract.signByAdvertiser(signerName, ipAddress);
        Contract saved = contractRepository.save(contract);

        if (saved.getStatus() == ContractStatus.SIGNED) {
            eventPublisher.publishEvent(new ContractSignedEvent(saved.getId(), saved.getBookingId(), LocalDateTime.now()));
        }
    }

    public Contract getContractById(UUID id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException("Contrat non trouvé avec l'id : " + id));
    }

    public Contract getContractByBookingId(UUID bookingId) {
        return contractRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ContractNotFoundException("Contrat non trouvé pour la réservation : " + bookingId));
    }

    public List<Contract> getContractsByOwner(UUID ownerId) {
        return contractRepository.findByOwnerId(ownerId);
    }

    public List<Contract> getContractsByAdvertiser(UUID advertiserId) {
        return contractRepository.findByAdvertiserId(advertiserId);
    }
}
