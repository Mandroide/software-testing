package com.amigoscode.testing.payment;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public interface CardPaymentCharger {
    CardPaymentCharge chargeCard(
            String cardSource,
            BigDecimal amount,
            Currency currency,
            String description);
}
