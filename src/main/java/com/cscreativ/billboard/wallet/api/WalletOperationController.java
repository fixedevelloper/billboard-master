package com.cscreativ.billboard.wallet.api;

import com.cscreativ.billboard.shared.AuthenticatedUser;
import com.cscreativ.billboard.wallet.api.mapper.WalletOperationMapper;
import com.cscreativ.billboard.wallet.api.request.DepositRequest;
import com.cscreativ.billboard.wallet.api.request.FailOperationRequest;
import com.cscreativ.billboard.wallet.api.request.WithdrawalRequest;
import com.cscreativ.billboard.wallet.api.response.PlatformBankAccountResponse;
import com.cscreativ.billboard.wallet.api.response.WalletOperationResponse;
import com.cscreativ.billboard.wallet.application.PlatformBankDetailsService;
import com.cscreativ.billboard.wallet.application.WalletOperationService;
import com.cscreativ.billboard.wallet.domain.WalletOperation;
import com.cscreativ.billboard.wallet.domain.WalletOperationMethod;
import com.cscreativ.billboard.wallet.domain.valueobject.BankDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/wallet-operations")
public class WalletOperationController {

    private final WalletOperationService operationService;
    private final PlatformBankDetailsService bankDetailsService;
    private final WalletOperationMapper mapper;

    public WalletOperationController(WalletOperationService operationService,
                                      PlatformBankDetailsService bankDetailsService,
                                      WalletOperationMapper mapper) {
        this.operationService = operationService;
        this.bankDetailsService = bankDetailsService;
        this.mapper = mapper;
    }

    /** Coordonnées de la plateforme à afficher pour un dépôt par virement (spécification 1.b). */
    @GetMapping("/bank-details")
    public ResponseEntity<PlatformBankAccountResponse> getPlatformBankDetails() {
        return ResponseEntity.ok(mapper.toResponse(bankDetailsService.getPlatformBankAccount()));
    }

    @PostMapping("/user/{userId}/deposits")
    public ResponseEntity<WalletOperationResponse> initiateDeposit(@PathVariable UUID userId, @RequestBody DepositRequest request,
                                                                     @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireSelfOrAdmin(userId);
        WalletOperation operation = operationService.initiateDeposit(userId, request.method(), request.amount(), request.currency(), request.phoneNumber());
        return ResponseEntity.ok(mapper.toResponse(operation));
    }

    @PostMapping("/user/{userId}/withdrawals")
    public ResponseEntity<WalletOperationResponse> initiateWithdrawal(@PathVariable UUID userId, @RequestBody WithdrawalRequest request,
                                                                        @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireSelfOrAdmin(userId);
        BankDetails bankDetails = request.method() == WalletOperationMethod.BANK_TRANSFER
                ? new BankDetails(request.bankAccountHolder(), request.bankIban(), request.bankName())
                : null;
        WalletOperation operation = operationService.initiateWithdrawal(userId, request.method(), request.amount(), request.currency(), request.phoneNumber(), bankDetails);
        return ResponseEntity.ok(mapper.toResponse(operation));
    }

    /** Confirme qu'une opération a abouti côté opérateur Mobile Money / banque. Admin uniquement : aucune passerelle automatisée n'est branchée pour l'instant. */
    @PutMapping("/{id}/complete")
    public ResponseEntity<WalletOperationResponse> complete(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        WalletOperation operation = operationService.completeOperation(id);
        return ResponseEntity.ok(mapper.toResponse(operation));
    }

    @PutMapping("/{id}/fail")
    public ResponseEntity<WalletOperationResponse> fail(@PathVariable UUID id, @RequestBody FailOperationRequest request,
                                                          @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        WalletOperation operation = operationService.failOperation(id, request.reason());
        return ResponseEntity.ok(mapper.toResponse(operation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletOperationResponse> getById(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        WalletOperation operation = operationService.getOperationById(id);
        currentUser.requireSelfOrAdmin(operation.getUserId());
        return ResponseEntity.ok(mapper.toResponse(operation));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WalletOperationResponse>> getByUser(@PathVariable UUID userId, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireSelfOrAdmin(userId);
        List<WalletOperation> operations = operationService.getOperationsByUser(userId);
        return ResponseEntity.ok(operations.stream().map(mapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping
    public ResponseEntity<List<WalletOperationResponse>> getAll(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        List<WalletOperation> operations = operationService.getAllOperations();
        return ResponseEntity.ok(operations.stream().map(mapper::toResponse).collect(Collectors.toList()));
    }
}
