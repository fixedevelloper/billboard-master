package com.cscreativ.billboard.wallet.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WalletResponse(
        UUID id,
        UUID userId,
        BigDecimal balance,
        String currency,
        LocalDateTime updatedAt
) {}
