package com.cscreativ.billboard.payment.api.mapper;

import com.cscreativ.billboard.payment.api.response.PaymentTransactionResponse;
import com.cscreativ.billboard.payment.domain.PaymentTransaction;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentTransactionResponse toResponse(PaymentTransaction transaction) {
        return new PaymentTransactionResponse(
                transaction.getId(),
                transaction.getPayerId(),
                transaction.getReferenceId(),
                transaction.getMoney().getAmount(),
                transaction.getMoney().getCurrency(),
                transaction.getPaymentMethod().name(),
                transaction.getStatus().name(),
                transaction.getGatewayReference(),
                transaction.getFailureReason(),
                transaction.getCreatedAt()
        );
    }
}
