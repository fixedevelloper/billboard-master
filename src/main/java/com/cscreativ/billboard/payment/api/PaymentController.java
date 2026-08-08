package com.cscreativ.billboard.payment.api;

import com.cscreativ.billboard.contract.ContractFacade;
import com.cscreativ.billboard.payment.api.mapper.PaymentMapper;
import com.cscreativ.billboard.payment.api.request.CompletePaymentRequest;
import com.cscreativ.billboard.payment.api.request.FlutterwaveCheckoutRequest;
import com.cscreativ.billboard.payment.api.request.InitiatePaymentRequest;
import com.cscreativ.billboard.payment.api.response.FlutterwaveCheckoutResponse;
import com.cscreativ.billboard.payment.api.response.PaymentTransactionResponse;
import com.cscreativ.billboard.payment.application.PaymentService;
import com.cscreativ.billboard.payment.domain.PaymentTransaction;
import com.cscreativ.billboard.shared.AuthenticatedUser;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;
    private final String webhookSecretHash;
    private final ContractFacade contractFacade;

    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper,
                              @Value("${flutterwave.webhook-secret-hash}") String webhookSecretHash,
                              ContractFacade contractFacade) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
        this.webhookSecretHash = webhookSecretHash;
        this.contractFacade = contractFacade;
    }

    @PostMapping("/initiate")
    public ResponseEntity<PaymentTransactionResponse> initiatePayment(@RequestBody InitiatePaymentRequest request,
                                                                       @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.require(currentUser.isAdmin() || currentUser.isAdvertiser(request.initiatedBy()));
        PaymentTransaction transaction = paymentService.initiatePayment(
                request.payerId(),
                request.referenceId(),
                request.amount(),
                request.currency(),
                request.paymentMethod(),
                request.initiatedBy()
        );
        return ResponseEntity.ok(paymentMapper.toResponse(transaction));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Void> completePayment(@PathVariable UUID id, @RequestBody CompletePaymentRequest request,
                                                 @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requirePartyToTransaction(paymentService.getTransactionById(id), currentUser);
        paymentService.completePayment(id, request.gatewayReference());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/flutterwave/checkout")
    public ResponseEntity<FlutterwaveCheckoutResponse> initiateFlutterwaveCheckout(
            @PathVariable UUID id, @RequestBody FlutterwaveCheckoutRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        requirePartyToTransaction(paymentService.getTransactionById(id), currentUser);
        String checkoutUrl = paymentService.initiateFlutterwaveCheckout(
                id, request.customerEmail(), request.customerName(), request.customerPhone());
        return ResponseEntity.ok(new FlutterwaveCheckoutResponse(checkoutUrl));
    }

    /** Retour navigateur après le paiement hébergé Flutterwave : vérifie puis rebondit vers le frontend. */
    @GetMapping("/flutterwave/callback")
    public void flutterwaveCallback(
            @RequestParam("tx_ref") UUID txRef,
            @RequestParam(value = "transaction_id", required = false) String flutterwaveTransactionId,
            @RequestParam(value = "status", required = false) String status,
            HttpServletResponse response) throws IOException {
        boolean reportedSuccess = "successful".equalsIgnoreCase(status) || "completed".equalsIgnoreCase(status);
        UUID bookingId = paymentService.handleFlutterwaveRedirect(txRef, flutterwaveTransactionId, reportedSuccess);
        response.sendRedirect(paymentService.getFrontendUrl() + "/bookings/" + bookingId
                + "/payment?flutterwave=" + (reportedSuccess ? "success" : "failed"));
    }

    /** Notification serveur-à-serveur Flutterwave : source de vérité indépendante de la redirection navigateur. */
    @PostMapping("/flutterwave/webhook")
    public ResponseEntity<Void> flutterwaveWebhook(
            @RequestHeader(value = "verif-hash", required = false) String verifHash,
            @RequestBody Map<String, Object> payload) {
        if (webhookSecretHash == null || webhookSecretHash.isBlank() || !webhookSecretHash.equals(verifHash)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Object dataObj = payload.get("data");
        if (dataObj instanceof Map<?, ?> data) {
            String flwTransactionId = String.valueOf(data.get("id"));
            String txRef = String.valueOf(data.get("tx_ref"));
            paymentService.handleFlutterwaveWebhook(flwTransactionId, txRef);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentTransactionResponse> getTransactionById(@PathVariable UUID id,
                                                                          @AuthenticationPrincipal AuthenticatedUser currentUser) {
        PaymentTransaction transaction = paymentService.getTransactionById(id);
        requirePartyToTransaction(transaction, currentUser);
        return ResponseEntity.ok(paymentMapper.toResponse(transaction));
    }

    @GetMapping("/payer/{payerId}")
    public ResponseEntity<List<PaymentTransactionResponse>> getTransactionsByPayer(@PathVariable UUID payerId,
                                                                                    @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.require(currentUser.isAdmin() || currentUser.isAdvertiser(payerId) || currentUser.isMediaBuyer(payerId));
        List<PaymentTransaction> transactions = paymentService.getTransactionsByPayer(payerId);
        return ResponseEntity.ok(transactions.stream().map(paymentMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/reference/{referenceId}")
    public ResponseEntity<List<PaymentTransactionResponse>> getTransactionsByReference(@PathVariable UUID referenceId,
                                                                                        @AuthenticationPrincipal AuthenticatedUser currentUser) {
        if (!currentUser.isAdmin()) {
            // referenceId est le bookingId (voir PaymentService.initiatePayment) : le contrat de cette
            // réservation porte déjà les deux parties légitimes, sans avoir à dépendre du module booking
            // (qui dépend lui-même de payment - la dépendance inverse créerait un cycle de modules).
            UUID advertiserId = contractFacade.findAdvertiserIdByBooking(referenceId).orElse(null);
            UUID ownerId = contractFacade.findOwnerIdByBooking(referenceId).orElse(null);
            currentUser.require(currentUser.isAdvertiser(advertiserId) || currentUser.isOwner(ownerId));
        }
        List<PaymentTransaction> transactions = paymentService.getTransactionsByReference(referenceId);
        return ResponseEntity.ok(transactions.stream().map(paymentMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping
    public ResponseEntity<List<PaymentTransactionResponse>> getAllTransactions(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        List<PaymentTransaction> transactions = paymentService.getAllTransactions();
        return ResponseEntity.ok(transactions.stream().map(paymentMapper::toResponse).collect(Collectors.toList()));
    }

    /** Payeur, media buyer délégué ou annonceur à l'initiative du paiement : les trois parties légitimes d'une transaction. */
    private void requirePartyToTransaction(PaymentTransaction transaction, AuthenticatedUser currentUser) {
        currentUser.require(currentUser.isAdmin()
                || currentUser.isAdvertiser(transaction.getPayerId())
                || currentUser.isMediaBuyer(transaction.getPayerId())
                || currentUser.isAdvertiser(transaction.getInitiatedBy()));
    }
}
