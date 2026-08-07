package com.cscreativ.billboard.payment.infrastructure.persistence;

import com.cscreativ.billboard.payment.domain.PaymentTransaction;
import com.cscreativ.billboard.payment.domain.repository.PaymentTransactionRepository;
import com.cscreativ.billboard.payment.domain.valueobject.Money;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PaymentTransactionPersistenceAdapter implements PaymentTransactionRepository {

    private final PaymentTransactionJpaRepository jpaRepository;

    public PaymentTransactionPersistenceAdapter(PaymentTransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PaymentTransaction save(PaymentTransaction transaction) {
        PaymentTransactionEntity entity = toEntity(transaction);
        PaymentTransactionEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PaymentTransaction> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<PaymentTransaction> findByPayerId(UUID payerId) {
        return jpaRepository.findByPayerId(payerId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<PaymentTransaction> findByReferenceId(UUID referenceId) {
        return jpaRepository.findByReferenceId(referenceId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<PaymentTransaction> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private PaymentTransactionEntity toEntity(PaymentTransaction domain) {
        PaymentTransactionEntity entity = new PaymentTransactionEntity();
        entity.setId(domain.getId());
        entity.setPayerId(domain.getPayerId());
        entity.setReferenceId(domain.getReferenceId());
        entity.setAmount(domain.getMoney().getAmount());
        entity.setCurrency(domain.getMoney().getCurrency());
        entity.setPaymentMethod(domain.getPaymentMethod());
        entity.setStatus(domain.getStatus());
        entity.setGatewayReference(domain.getGatewayReference());
        entity.setFailureReason(domain.getFailureReason());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private PaymentTransaction toDomain(PaymentTransactionEntity entity) {
        Money money = new Money(entity.getAmount(), entity.getCurrency());
        return new PaymentTransaction(
                entity.getId(),
                entity.getPayerId(),
                entity.getReferenceId(),
                money,
                entity.getPaymentMethod(),
                entity.getStatus(),
                entity.getGatewayReference(),
                entity.getFailureReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
