package com.amigoscode.testing.payment;

import org.springframework.stereotype.Component;

@Component
public interface SMSSender {
    SMSSent sendSMS(String phoneNumber, String message);
}
