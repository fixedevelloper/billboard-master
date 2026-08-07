package com.cscreativ.billboard.payment.domain.repository;

import com.cscreativ.billboard.payment.domain.PaymentTransaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentTransactionRepository {
    PaymentTransaction save(PaymentTransaction transaction);
    Optional<PaymentTransaction> findById(UUID id);
    List<PaymentTransaction> findByPayerId(UUID payerId);
    List<PaymentTransaction> findByReferenceId(UUID referenceId);
    List<PaymentTransaction> findAll();
}
