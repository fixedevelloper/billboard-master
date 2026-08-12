package com.cscreativ.billboard.wallet.application;

/** Coordonnées bancaires de la plateforme à afficher pour un dépôt par virement (voir PlatformBankDetailsService). */
public record PlatformBankAccount(String accountHolderName, String iban, String bankName) {
}
