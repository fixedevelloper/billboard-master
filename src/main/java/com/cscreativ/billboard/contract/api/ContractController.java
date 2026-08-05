package com.cscreativ.billboard.contract.api;

import com.cscreativ.billboard.contract.api.mapper.ContractMapper;
import com.cscreativ.billboard.contract.api.request.CreateContractRequest;
import com.cscreativ.billboard.contract.api.request.SignContractRequest;
import com.cscreativ.billboard.contract.api.response.ContractResponse;
import com.cscreativ.billboard.contract.application.ContractService;
import com.cscreativ.billboard.contract.domain.Contract;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ContractResponse> createContract(@RequestBody CreateContractRequest request) {
        Contract contract = contractService.createContract(
                request.bookingId(),
                request.ownerId(),
                request.advertiserId(),
                request.termsAndConditions()
        );
        return ResponseEntity.ok(contractMapper.toResponse(contract));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Void> publishForSignature(@PathVariable UUID id) {
        contractService.publishForSignature(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sign/owner")
    public ResponseEntity<Void> signByOwner(@PathVariable UUID id, @RequestBody SignContractRequest request) {
        contractService.signByOwner(id, request.signerName(), request.ipAddress());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sign/advertiser")
    public ResponseEntity<Void> signByAdvertiser(@PathVariable UUID id, @RequestBody SignContractRequest request) {
        contractService.signByAdvertiser(id, request.signerName(), request.ipAddress());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponse> getContractById(@PathVariable UUID id) {
        Contract contract = contractService.getContractById(id);
        return ResponseEntity.ok(contractMapper.toResponse(contract));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ContractResponse> getContractByBookingId(@PathVariable UUID bookingId) {
        Contract contract = contractService.getContractByBookingId(bookingId);
        return ResponseEntity.ok(contractMapper.toResponse(contract));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ContractResponse>> getContractsByOwner(@PathVariable UUID ownerId) {
        List<Contract> contracts = contractService.getContractsByOwner(ownerId);
        return ResponseEntity.ok(contracts.stream().map(contractMapper::toResponse).collect(Collectors.toList()));
    }
}
