package com.cscreativ.billboard.wallet.api.mapper;

import com.cscreativ.billboard.wallet.api.response.PlatformBankAccountResponse;
import com.cscreativ.billboard.wallet.api.response.WalletOperationResponse;
import com.cscreativ.billboard.wallet.application.PlatformBankAccount;
import com.cscreativ.billboard.wallet.domain.WalletOperation;
import com.cscreativ.billboard.wallet.domain.valueobject.BankDetails;
import org.springframework.stereotype.Component;

@Component
public class WalletOperationMapper {

    public WalletOperationResponse toResponse(WalletOperation operation) {
        BankDetails bankDetails = operation.getBankDetails();
        return new WalletOperationResponse(
                operation.getId(),
                operation.getUserId(),
                operation.getType().name(),
                operation.getMethod().name(),
                operation.getStatus().name(),
                operation.getAmount(),
                operation.getCurrency(),
                operation.getPhoneNumber(),
                bankDetails == null ? null : bankDetails.getAccountHolderName(),
                bankDetails == null ? null : bankDetails.getIban(),
                bankDetails == null ? null : bankDetails.getBankName(),
                operation.getReference(),
                operation.getFailureReason(),
                operation.getCreatedAt(),
                operation.getUpdatedAt()
        );
    }

    public PlatformBankAccountResponse toResponse(PlatformBankAccount account) {
        return new PlatformBankAccountResponse(account.accountHolderName(), account.iban(), account.bankName());
    }
}
