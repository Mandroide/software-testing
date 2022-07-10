package com.amigoscode.testing.payment.twilio;

import com.amigoscode.testing.payment.SMSSender;
import com.amigoscode.testing.payment.SMSSent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "twilio.enabled", havingValue = "false")
public class MockTwilioService implements SMSSender {
    @Override
    public SMSSent sendSMS(String phoneNumber, String message) {
        return new SMSSent(true);
    }
}
