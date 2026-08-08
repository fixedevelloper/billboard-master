package com.cscreativ.billboard.contract.api;

import com.cscreativ.billboard.contract.api.mapper.ContractMapper;
import com.cscreativ.billboard.contract.api.request.CreateContractRequest;
import com.cscreativ.billboard.contract.api.request.SignContractRequest;
import com.cscreativ.billboard.contract.api.response.ContractResponse;
import com.cscreativ.billboard.contract.application.ContractService;
import com.cscreativ.billboard.contract.domain.Contract;
import com.cscreativ.billboard.shared.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/contracts")
public class ContractController {

    private final ContractService contractService;
    private final ContractMapper contractMapper;

    public ContractController(ContractService contractService, ContractMapper contractMapper) {
        this.contractService = contractService;
        this.contractMapper = contractMapper;
    }

    @PostMapping
    public ResponseEntity<ContractResponse> createContract(@RequestBody CreateContractRequest request,
                                                            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.require(currentUser.isAdmin()
                || currentUser.isOwner(request.ownerId())
                || currentUser.isAdvertiser(request.advertiserId()));
        Contract contract = contractService.createContract(
                request.bookingId(),
                request.ownerId(),
                request.advertiserId(),
                request.termsAndConditions()
        );
        return ResponseEntity.ok(contractMapper.toResponse(contract));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Void> publishForSignature(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requirePartyToContract(contractService.getContractById(id), currentUser);
        contractService.publishForSignature(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sign/owner")
    public ResponseEntity<Void> signByOwner(@PathVariable UUID id, @RequestBody SignContractRequest request,
                                             @AuthenticationPrincipal AuthenticatedUser currentUser) {
        Contract contract = contractService.getContractById(id);
        currentUser.require(currentUser.isOwner(contract.getOwnerId()));
        contractService.signByOwner(id, request.signerName(), request.ipAddress());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sign/advertiser")
    public ResponseEntity<Void> signByAdvertiser(@PathVariable UUID id, @RequestBody SignContractRequest request,
                                                  @AuthenticationPrincipal AuthenticatedUser currentUser) {
        Contract contract = contractService.getContractById(id);
        currentUser.require(currentUser.isAdvertiser(contract.getAdvertiserId()));
        contractService.signByAdvertiser(id, request.signerName(), request.ipAddress());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponse> getContractById(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        Contract contract = contractService.getContractById(id);
        requirePartyToContract(contract, currentUser);
        return ResponseEntity.ok(contractMapper.toResponse(contract));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ContractResponse> getContractByBookingId(@PathVariable UUID bookingId,
                                                                    @AuthenticationPrincipal AuthenticatedUser currentUser) {
        Contract contract = contractService.getContractByBookingId(bookingId);
        requirePartyToContract(contract, currentUser);
        return ResponseEntity.ok(contractMapper.toResponse(contract));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ContractResponse>> getContractsByOwner(@PathVariable UUID ownerId,
                                                                       @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.require(currentUser.isAdmin() || currentUser.isOwner(ownerId));
        List<Contract> contracts = contractService.getContractsByOwner(ownerId);
        return ResponseEntity.ok(contracts.stream().map(contractMapper::toResponse).collect(Collectors.toList()));
    }

    private void requirePartyToContract(Contract contract, AuthenticatedUser currentUser) {
        currentUser.require(currentUser.isAdmin()
                || currentUser.isOwner(contract.getOwnerId())
                || currentUser.isAdvertiser(contract.getAdvertiserId()));
    }
}
