package com.cscreativ.billboard.shared;

import com.cscreativ.billboard.shared.domain.valueobject.Money;
import java.util.Currency;

public interface SharedFacade {
    Money createMoney(double amount, String currencyCode);
}
