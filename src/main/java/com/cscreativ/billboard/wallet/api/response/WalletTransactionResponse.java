package com.cscreativ.billboard.wallet.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WalletTransactionResponse(
        UUID id,
        UUID walletId,
        String type,
        BigDecimal amount,
        String currency,
        String reference,
        LocalDateTime createdAt
) {}
