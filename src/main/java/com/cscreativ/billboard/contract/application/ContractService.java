package com.cscreativ.billboard.contract.application;

import com.cscreativ.billboard.advertiser.AdvertiserFacade;
import com.cscreativ.billboard.contract.domain.Contract;
import com.cscreativ.billboard.contract.domain.ContractStatus;
import com.cscreativ.billboard.contract.domain.exception.ContractNotFoundException;
import com.cscreativ.billboard.contract.domain.repository.ContractRepository;
import com.cscreativ.billboard.contract.events.ContractCreatedEvent;
import com.cscreativ.billboard.contract.events.ContractSignedEvent;
import com.cscreativ.billboard.notification.NotificationFacade;
import com.cscreativ.billboard.owner.OwnerFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContractService {

    private static final Logger log = LoggerFactory.getLogger(ContractService.class);

    private final ContractRepository contractRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final OwnerFacade ownerFacade;
    private final AdvertiserFacade advertiserFacade;
    private final NotificationFacade notificationFacade;

    public ContractService(ContractRepository contractRepository, ApplicationEventPublisher eventPublisher,
                            OwnerFacade ownerFacade, AdvertiserFacade advertiserFacade, NotificationFacade notificationFacade) {
        this.contractRepository = contractRepository;
        this.eventPublisher = eventPublisher;
        this.ownerFacade = ownerFacade;
        this.advertiserFacade = advertiserFacade;
        this.notificationFacade = notificationFacade;
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
        Contract saved = contractRepository.save(contract);

        notifyOwner(saved, "CONTRACT_READY_FOR_SIGNATURE");
        notifyAdvertiser(saved, "CONTRACT_READY_FOR_SIGNATURE");
    }

    @Transactional
    public void signByOwner(UUID contractId, String signerName, String ipAddress) {
        Contract contract = getContractById(contractId);
        contract.signByOwner(signerName, ipAddress);
        Contract saved = contractRepository.save(contract);

        if (saved.getStatus() == ContractStatus.SIGNED) {
            eventPublisher.publishEvent(new ContractSignedEvent(saved.getId(), saved.getBookingId(), LocalDateTime.now()));
            notifyOwner(saved, "CONTRACT_FULLY_SIGNED");
            notifyAdvertiser(saved, "CONTRACT_FULLY_SIGNED");
        } else {
            notifyAdvertiser(saved, "CONTRACT_SIGNATURE_YOUR_TURN");
        }
    }

    @Transactional
    public void signByAdvertiser(UUID contractId, String signerName, String ipAddress) {
        Contract contract = getContractById(contractId);
        contract.signByAdvertiser(signerName, ipAddress);
        Contract saved = contractRepository.save(contract);

        if (saved.getStatus() == ContractStatus.SIGNED) {
            eventPublisher.publishEvent(new ContractSignedEvent(saved.getId(), saved.getBookingId(), LocalDateTime.now()));
            notifyOwner(saved, "CONTRACT_FULLY_SIGNED");
            notifyAdvertiser(saved, "CONTRACT_FULLY_SIGNED");
        } else {
            notifyOwner(saved, "CONTRACT_SIGNATURE_YOUR_TURN");
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

    private void notifyOwner(Contract contract, String templateCode) {
        Optional<UUID> recipientUserId = ownerFacade.findUserIdByOwnerId(contract.getOwnerId());
        Optional<String> recipientEmail = ownerFacade.findContactEmailByOwnerId(contract.getOwnerId());
        if (recipientUserId.isEmpty() || recipientEmail.isEmpty()) {
            log.warn("Contrat {} : propriétaire introuvable ({}), notification {} non envoyée", contract.getId(), contract.getOwnerId(), templateCode);
            return;
        }
        notificationFacade.sendNotification(recipientUserId.get(), recipientEmail.get(), templateCode, "EMAIL", contractParams(contract));
    }

    private void notifyAdvertiser(Contract contract, String templateCode) {
        Optional<UUID> recipientUserId = advertiserFacade.findUserIdByAdvertiserId(contract.getAdvertiserId());
        Optional<String> recipientEmail = advertiserFacade.findContactEmailByAdvertiserId(contract.getAdvertiserId());
        if (recipientUserId.isEmpty() || recipientEmail.isEmpty()) {
            log.warn("Contrat {} : annonceur introuvable ({}), notification {} non envoyée", contract.getId(), contract.getAdvertiserId(), templateCode);
            return;
        }
        notificationFacade.sendNotification(recipientUserId.get(), recipientEmail.get(), templateCode, "EMAIL", contractParams(contract));
    }

    private Map<String, String> contractParams(Contract contract) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("Référence de réservation", contract.getBookingId().toString());
        params.put("Statut", contract.getStatus().name());
        return params;
    }
}
