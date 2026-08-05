package com.cscreativ.billboard.payment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentTransactionJpaRepository extends JpaRepository<PaymentTransactionEntity, UUID> {
    List<PaymentTransactionEntity> findByPayerId(UUID payerId);
    List<PaymentTransactionEntity> findByReferenceId(UUID referenceId);
}
