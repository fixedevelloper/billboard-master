package com.cscreativ.billboard.wallet.application;

import com.cscreativ.billboard.wallet.domain.Wallet;
import com.cscreativ.billboard.wallet.domain.WalletTransaction;
import com.cscreativ.billboard.wallet.domain.WalletTransactionType;
import com.cscreativ.billboard.wallet.domain.exception.WalletNotFoundException;
import com.cscreativ.billboard.wallet.domain.repository.WalletRepository;
import com.cscreativ.billboard.wallet.domain.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository, WalletTransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Wallet getOrCreateWallet(UUID userId, String currency) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> walletRepository.save(Wallet.createEmpty(userId, currency)));
    }

    public Wallet getWalletByUserId(UUID userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Aucun portefeuille trouvé pour l'utilisateur : " + userId));
    }

    @Transactional
    public WalletTransaction deposit(UUID userId, BigDecimal amount, String currency, String reference) {
        Wallet wallet = getOrCreateWallet(userId, currency);
        wallet.deposit(amount);
        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.create(wallet.getId(), WalletTransactionType.DEPOSIT, amount, currency, reference);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public WalletTransaction withdraw(UUID userId, BigDecimal amount, String currency, String reference) {
        Wallet wallet = getWalletByUserId(userId);
        wallet.withdraw(amount);
        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.create(wallet.getId(), WalletTransactionType.WITHDRAWAL, amount, currency, reference);
        return transactionRepository.save(transaction);
    }

    public List<WalletTransaction> getTransactions(UUID userId) {
        return walletRepository.findByUserId(userId)
                .map(wallet -> transactionRepository.findByWalletId(wallet.getId()))
                .orElseGet(List::of);
    }
}
