package com.amigoscode.testing.payment.twilio;

import com.amigoscode.testing.payment.SMSSender;
import com.amigoscode.testing.payment.SMSSent;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "twilio.enabled", havingValue = "true")
public class TwilioService implements SMSSender {
    private final TwilioApi twilioApi;
    private static final PhoneNumber from = new PhoneNumber("+000000");
    @Autowired
    public TwilioService(TwilioApi twilioApi) {
        this.twilioApi = twilioApi;
    }

    @Override
    public SMSSent sendSMS(String phoneNumber, String message) {
        PhoneNumber to = new PhoneNumber(phoneNumber);
        Message twilioApiMessage = twilioApi.createMessage(to, from, message);
        return new SMSSent(twilioApiMessage.getStatus() == Message.Status.SENT);
    }
}
