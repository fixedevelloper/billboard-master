package com.cscreativ.billboard.wallet.domain.valueobject;

/**
 * Coordonnées bancaires du destinataire d'un retrait par virement (method = BANK_TRANSFER).
 * Pour un dépôt par virement, ce sont au contraire les coordonnées de la plateforme qui sont
 * affichées à l'utilisateur — voir platformsettings (clés bank.*), pas ce value object.
 */
public class BankDetails {
    private final String accountHolderName;
    private final String iban;
    private final String bankName;

    public BankDetails(String accountHolderName, String iban, String bankName) {
        if (accountHolderName == null || accountHolderName.isBlank()
                || iban == null || iban.isBlank()
                || bankName == null || bankName.isBlank()) {
            throw new IllegalArgumentException("Le nom du titulaire, l'IBAN/numéro de compte et la banque sont obligatoires pour un retrait par virement");
        }
        this.accountHolderName = accountHolderName;
        this.iban = iban;
        this.bankName = bankName;
    }

    public String getAccountHolderName() { return accountHolderName; }
    public String getIban() { return iban; }
    public String getBankName() { return bankName; }
}
