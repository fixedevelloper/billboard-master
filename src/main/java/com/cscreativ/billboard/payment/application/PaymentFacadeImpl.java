package com.cscreativ.billboard.payment.application;

import com.cscreativ.billboard.payment.PaymentFacade;
import com.cscreativ.billboard.payment.domain.PaymentStatus;
import com.cscreativ.billboard.payment.domain.PaymentTransaction;
import com.cscreativ.billboard.payment.domain.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentFacadeImpl implements PaymentFacade {

    private final PaymentTransactionRepository transactionRepository;

    public PaymentFacadeImpl(PaymentTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Optional<PaymentTransaction> findTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId);
    }

    @Override
    public boolean isPaymentCompleted(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .map(t -> t.getStatus() == PaymentStatus.SUCCESSFUL)
                .orElse(false);
    }
}
