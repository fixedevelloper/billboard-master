package com.cscreativ.billboard.wallet.api;

import com.cscreativ.billboard.wallet.api.mapper.WalletMapper;
import com.cscreativ.billboard.wallet.api.response.WalletResponse;
import com.cscreativ.billboard.wallet.api.response.WalletTransactionResponse;
import com.cscreativ.billboard.shared.AuthenticatedUser;
import com.cscreativ.billboard.wallet.application.WalletService;
import com.cscreativ.billboard.wallet.domain.Wallet;
import com.cscreativ.billboard.wallet.domain.WalletTransaction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletService walletService;
    private final WalletMapper walletMapper;

    public WalletController(WalletService walletService, WalletMapper walletMapper) {
        this.walletService = walletService;
        this.walletMapper = walletMapper;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable UUID userId, @RequestParam(defaultValue = "XOF") String currency,
                                                     @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireSelfOrAdmin(userId);
        Wallet wallet = walletService.getOrCreateWallet(userId, currency);
        return ResponseEntity.ok(walletMapper.toResponse(wallet));
    }

    /**
     * Le dépôt/retrait "instantané" a été remplacé par le parcours complet Mobile
     * Money/virement bancaire avec statut PENDING/COMPLETED/FAILED — voir WalletOperationController
     * (POST /api/v1/wallet-operations/deposits|withdrawals).
     */
    @GetMapping("/user/{userId}/transactions")
    public ResponseEntity<List<WalletTransactionResponse>> getTransactions(@PathVariable UUID userId,
                                                                            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireSelfOrAdmin(userId);
        List<WalletTransaction> transactions = walletService.getTransactions(userId);
        return ResponseEntity.ok(transactions.stream().map(walletMapper::toResponse).collect(Collectors.toList()));
    }
}
