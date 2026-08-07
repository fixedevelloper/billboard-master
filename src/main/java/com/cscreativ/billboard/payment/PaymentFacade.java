package com.cscreativ.billboard.payment;

import com.cscreativ.billboard.payment.domain.PaymentTransaction;

import java.util.Optional;
import java.util.UUID;

public interface PaymentFacade {
    Optional<PaymentTransaction> findTransactionById(UUID transactionId);
    boolean isPaymentCompleted(UUID transactionId);
    boolean hasCompletedPaymentForReference(UUID referenceId);
}
