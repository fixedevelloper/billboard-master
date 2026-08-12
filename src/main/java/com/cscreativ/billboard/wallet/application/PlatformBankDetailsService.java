package com.cscreativ.billboard.wallet.application;

import com.cscreativ.billboard.platformsettings.ConfigFacade;
import org.springframework.stereotype.Service;

/**
 * Coordonnées bancaires de la plateforme, configurables par un admin via
 * POST /api/v1/configs (clés bank.account-holder / bank.iban / bank.name — voir platformsettings)
 * plutôt qu'en dur : affichées à l'utilisateur pour un dépôt par virement (spécification 1.b).
 * Champs vides tant qu'un admin ne les a pas renseignés — le frontend l'indique clairement plutôt
 * que de faire échouer tout le parcours de dépôt par virement.
 */
@Service
public class PlatformBankDetailsService {

    static final String KEY_ACCOUNT_HOLDER = "bank.account-holder";
    static final String KEY_IBAN = "bank.iban";
    static final String KEY_NAME = "bank.name";

    private final ConfigFacade configFacade;

    public PlatformBankDetailsService(ConfigFacade configFacade) {
        this.configFacade = configFacade;
    }

    public PlatformBankAccount getPlatformBankAccount() {
        return new PlatformBankAccount(
                configFacade.getSettingValueOrDefault(KEY_ACCOUNT_HOLDER, ""),
                configFacade.getSettingValueOrDefault(KEY_IBAN, ""),
                configFacade.getSettingValueOrDefault(KEY_NAME, "")
        );
    }
}
