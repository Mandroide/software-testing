package com.amigoscode.testing.payment.twilio;

import com.twilio.Twilio;
import org.springframework.stereotype.Service;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class TwilioApi {
    private static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    private static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");

    public Message createMessage(PhoneNumber to, PhoneNumber from, String message){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        return Message.creator(to, from, message).create();
    }
}
