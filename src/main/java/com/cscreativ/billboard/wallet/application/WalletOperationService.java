package com.cscreativ.billboard.wallet.application;

import com.cscreativ.billboard.wallet.WalletFacade;
import com.cscreativ.billboard.wallet.domain.Wallet;
import com.cscreativ.billboard.wallet.domain.WalletOperation;
import com.cscreativ.billboard.wallet.domain.WalletOperationMethod;
import com.cscreativ.billboard.wallet.domain.WalletTransaction;
import com.cscreativ.billboard.wallet.domain.WalletTransactionType;
import com.cscreativ.billboard.wallet.domain.exception.InsufficientBalanceException;
import com.cscreativ.billboard.wallet.domain.exception.WalletOperationNotFoundException;
import com.cscreativ.billboard.wallet.domain.repository.WalletOperationRepository;
import com.cscreativ.billboard.wallet.domain.repository.WalletRepository;
import com.cscreativ.billboard.wallet.domain.repository.WalletTransactionRepository;
import com.cscreativ.billboard.wallet.domain.valueobject.BankDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletOperationService {

    private final WalletOperationRepository operationRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final WalletFacade walletFacade;

    public WalletOperationService(WalletOperationRepository operationRepository,
                                   WalletRepository walletRepository,
                                   WalletTransactionRepository transactionRepository,
                                   WalletFacade walletFacade) {
        this.operationRepository = operationRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.walletFacade = walletFacade;
    }

    /** Aucun mouvement de solde ici : le dépôt ne crédite qu'à la complétion (argent confirmé reçu). */
    @Transactional
    public WalletOperation initiateDeposit(UUID userId, WalletOperationMethod method, BigDecimal amount,
                                            String currency, String phoneNumber) {
        WalletOperation operation = WalletOperation.initiateDeposit(userId, method, amount, currency, phoneNumber);
        return operationRepository.save(operation);
    }

    /**
     * Débite le portefeuille immédiatement, dans la même transaction que la création de la
     * demande : le verrou pessimiste (findByUserIdForUpdate) empêche deux retraits concurrents
     * de lire le même solde avant que l'un des deux ne l'ait débité — sans quoi les deux
     * pourraient passer la vérification de solde et le portefeuille finirait négatif.
     * COMPLETED (voir completeOperation) ne fait alors que confirmer l'envoi effectif des fonds ;
     * FAILED (voir failOperation) rembourse ce montant.
     */
    @Transactional
    public WalletOperation initiateWithdrawal(UUID userId, WalletOperationMethod method, BigDecimal amount,
                                               String currency, String phoneNumber, BankDetails bankDetails) {
        WalletOperation operation = WalletOperation.initiateWithdrawal(userId, method, amount, currency, phoneNumber, bankDetails);

        Wallet wallet = walletRepository.findByUserIdForUpdate(userId).orElse(null);
        if (wallet == null || wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Solde insuffisant pour effectuer ce retrait");
        }
        wallet.withdraw(amount);
        walletRepository.save(wallet);

        WalletOperation saved = operationRepository.save(operation);
        transactionRepository.save(WalletTransaction.create(
                wallet.getId(), WalletTransactionType.WITHDRAWAL, amount, currency, operationReference(saved)));

        return saved;
    }

    /** DEPOSIT -> crédite le portefeuille maintenant. WITHDRAWAL -> déjà débité, rien de plus. */
    @Transactional
    public WalletOperation completeOperation(UUID operationId) {
        WalletOperation operation = getOperationById(operationId);
        operation.complete();
        WalletOperation saved = operationRepository.save(operation);

        if (saved.getType() == WalletTransactionType.DEPOSIT) {
            walletFacade.creditIfAbsent(saved.getUserId(), saved.getAmount(), saved.getCurrency(), operationReference(saved));
        }

        return saved;
    }

    /** WITHDRAWAL -> rembourse le montant déjà débité à l'initiation. DEPOSIT -> rien à annuler. */
    @Transactional
    public WalletOperation failOperation(UUID operationId, String reason) {
        WalletOperation operation = getOperationById(operationId);
        operation.fail(reason);
        WalletOperation saved = operationRepository.save(operation);

        if (saved.getType() == WalletTransactionType.WITHDRAWAL) {
            walletFacade.creditIfAbsent(saved.getUserId(), saved.getAmount(), saved.getCurrency(), operationReference(saved) + ":refund");
        }

        return saved;
    }

    public WalletOperation getOperationById(UUID id) {
        return operationRepository.findById(id)
                .orElseThrow(() -> new WalletOperationNotFoundException("Opération introuvable avec l'id : " + id));
    }

    public List<WalletOperation> getOperationsByUser(UUID userId) {
        return operationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<WalletOperation> getAllOperations() {
        return operationRepository.findAllByOrderByCreatedAtDesc();
    }

    private static String operationReference(WalletOperation operation) {
        return "wallet-operation:" + operation.getId();
    }
}
