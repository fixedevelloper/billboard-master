package com.cscreativ.billboard.wallet.api;

import com.cscreativ.billboard.wallet.api.mapper.WalletMapper;
import com.cscreativ.billboard.wallet.api.request.DepositRequest;
import com.cscreativ.billboard.wallet.api.request.WithdrawRequest;
import com.cscreativ.billboard.wallet.api.response.WalletResponse;
import com.cscreativ.billboard.wallet.api.response.WalletTransactionResponse;
import com.cscreativ.billboard.wallet.application.WalletService;
import com.cscreativ.billboard.wallet.domain.Wallet;
import com.cscreativ.billboard.wallet.domain.WalletTransaction;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<WalletResponse> getWallet(@PathVariable UUID userId, @RequestParam(defaultValue = "XOF") String currency) {
        Wallet wallet = walletService.getOrCreateWallet(userId, currency);
        return ResponseEntity.ok(walletMapper.toResponse(wallet));
    }

    @PostMapping("/user/{userId}/deposit")
    public ResponseEntity<WalletResponse> deposit(@PathVariable UUID userId, @RequestBody DepositRequest request) {
        walletService.deposit(userId, request.amount(), request.currency(), request.reference());
        Wallet wallet = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(walletMapper.toResponse(wallet));
    }

    @PostMapping("/user/{userId}/withdraw")
    public ResponseEntity<WalletResponse> withdraw(@PathVariable UUID userId, @RequestBody WithdrawRequest request) {
        walletService.withdraw(userId, request.amount(), request.currency(), request.reference());
        Wallet wallet = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(walletMapper.toResponse(wallet));
    }

    @GetMapping("/user/{userId}/transactions")
    public ResponseEntity<List<WalletTransactionResponse>> getTransactions(@PathVariable UUID userId) {
        List<WalletTransaction> transactions = walletService.getTransactions(userId);
        return ResponseEntity.ok(transactions.stream().map(walletMapper::toResponse).collect(Collectors.toList()));
    }
}
