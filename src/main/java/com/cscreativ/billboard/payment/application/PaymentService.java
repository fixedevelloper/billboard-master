package com.cscreativ.billboard.payment.application;

import com.cscreativ.billboard.payment.domain.PaymentMethod;
import com.cscreativ.billboard.payment.domain.PaymentTransaction;
import com.cscreativ.billboard.payment.domain.exception.PaymentTransactionNotFoundException;
import com.cscreativ.billboard.payment.domain.repository.PaymentTransactionRepository;
import com.cscreativ.billboard.payment.domain.valueobject.Money;
import com.cscreativ.billboard.payment.events.PaymentCompletedEvent;
import com.cscreativ.billboard.payment.events.PaymentFailedEvent;
import com.cscreativ.billboard.payment.events.PaymentInitiatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentTransactionRepository transactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentService(PaymentTransactionRepository transactionRepository, ApplicationEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PaymentTransaction initiatePayment(UUID payerId, UUID referenceId, BigDecimal amount, String currency, PaymentMethod paymentMethod) {
        Money money = new Money(amount, currency);
        PaymentTransaction transaction = PaymentTransaction.initiate(payerId, referenceId, money, paymentMethod);
        PaymentTransaction saved = transactionRepository.save(transaction);

        eventPublisher.publishEvent(new PaymentInitiatedEvent(
                saved.getId(), saved.getPayerId(), saved.getReferenceId(), saved.getMoney().getAmount(), saved.getMoney().getCurrency(), LocalDateTime.now()
        ));
        return saved;
    }

    @Transactional
    public void completePayment(UUID transactionId, String gatewayReference) {
        PaymentTransaction transaction = getTransactionById(transactionId);
        transaction.markAsSuccessful(gatewayReference);
        PaymentTransaction saved = transactionRepository.save(transaction);

        eventPublisher.publishEvent(new PaymentCompletedEvent(
                saved.getId(), saved.getPayerId(), saved.getReferenceId(), saved.getMoney().getAmount(), gatewayReference, LocalDateTime.now()
        ));
    }

    @Transactional
    public void failPayment(UUID transactionId, String reason) {
        PaymentTransaction transaction = getTransactionById(transactionId);
        transaction.markAsFailed(reason);
        PaymentTransaction saved = transactionRepository.save(transaction);

        eventPublisher.publishEvent(new PaymentFailedEvent(saved.getId(), saved.getPayerId(), reason, LocalDateTime.now()));
    }

    public PaymentTransaction getTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new PaymentTransactionNotFoundException("Transaction de paiement non trouvée avec l'id : " + id));
    }

    public List<PaymentTransaction> getTransactionsByPayer(UUID payerId) {
        return transactionRepository.findByPayerId(payerId);
    }
}
