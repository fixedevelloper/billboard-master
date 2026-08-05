package com.cscreativ.billboard.payment.api;

import com.cscreativ.billboard.payment.api.mapper.PaymentMapper;
import com.cscreativ.billboard.payment.api.request.CompletePaymentRequest;
import com.cscreativ.billboard.payment.api.request.InitiatePaymentRequest;
import com.cscreativ.billboard.payment.api.response.PaymentTransactionResponse;
import com.cscreativ.billboard.payment.application.PaymentService;
import com.cscreativ.billboard.payment.domain.PaymentTransaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping("/initiate")
    public ResponseEntity<PaymentTransactionResponse> initiatePayment(@RequestBody InitiatePaymentRequest request) {
        PaymentTransaction transaction = paymentService.initiatePayment(
                request.payerId(),
                request.referenceId(),
                request.amount(),
                request.currency(),
                request.paymentMethod()
        );
        return ResponseEntity.ok(paymentMapper.toResponse(transaction));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Void> completePayment(@PathVariable UUID id, @RequestBody CompletePaymentRequest request) {
        paymentService.completePayment(id, request.gatewayReference());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentTransactionResponse> getTransactionById(@PathVariable UUID id) {
        PaymentTransaction transaction = paymentService.getTransactionById(id);
        return ResponseEntity.ok(paymentMapper.toResponse(transaction));
    }

    @GetMapping("/payer/{payerId}")
    public ResponseEntity<List<PaymentTransactionResponse>> getTransactionsByPayer(@PathVariable UUID payerId) {
        List<PaymentTransaction> transactions = paymentService.getTransactionsByPayer(payerId);
        return ResponseEntity.ok(transactions.stream().map(paymentMapper::toResponse).collect(Collectors.toList()));
    }
}
