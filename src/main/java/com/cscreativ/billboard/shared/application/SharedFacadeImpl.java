package com.cscreativ.billboard.shared.application;

import com.cscreativ.billboard.shared.SharedFacade;
import com.cscreativ.billboard.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

@Component
public class SharedFacadeImpl implements SharedFacade {

    @Override
    public Money createMoney(double amount, String currencyCode) {
        return Money.of(amount, currencyCode);
    }
}
