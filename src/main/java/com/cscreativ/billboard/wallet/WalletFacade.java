package com.cscreativ.billboard.wallet;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletFacade {

    /**
     * Crédite le portefeuille de userId (créé au besoin), sauf si un mouvement de type DEPOSIT
     * portant déjà exactement cette référence existe sur ce portefeuille — no-op silencieux dans
     * ce cas. Pensé pour des appelants pilotés par événement (voir booking.RevenueSplitListener) :
     * une redélivrance de l'événement déclencheur ne doit jamais créditer deux fois.
     */
    void creditIfAbsent(UUID userId, BigDecimal amount, String currency, String reference);
}
