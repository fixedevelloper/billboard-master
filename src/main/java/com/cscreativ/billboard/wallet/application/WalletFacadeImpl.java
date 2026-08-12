package com.cscreativ.billboard.wallet.application;

import com.cscreativ.billboard.wallet.WalletFacade;
import com.cscreativ.billboard.wallet.domain.Wallet;
import com.cscreativ.billboard.wallet.domain.WalletTransactionType;
import com.cscreativ.billboard.wallet.domain.repository.WalletTransactionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class WalletFacadeImpl implements WalletFacade {

    private final WalletService walletService;
    private final WalletTransactionRepository transactionRepository;

    public WalletFacadeImpl(WalletService walletService, WalletTransactionRepository transactionRepository) {
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public void creditIfAbsent(UUID userId, BigDecimal amount, String currency, String reference) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        Wallet wallet = walletService.getOrCreateWallet(userId, currency);
        if (transactionRepository.existsByWalletIdAndTypeAndReference(wallet.getId(), WalletTransactionType.DEPOSIT, reference)) {
            return;
        }
        walletService.deposit(userId, amount, currency, reference);
    }
}
