package com.amigoscode.testing.payment.twilio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class TwilioServiceTest {
    @Mock
    private TwilioApi twilioApi;

    private TwilioService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new TwilioService(twilioApi);
    }

    @Test
    void sendSMS_WhenCalled_SMSIsSent() {
        // Given
        String body = "body";
        PhoneNumber phoneNumber = new PhoneNumber("+0");
        var message = Message.fromJson("{\"status\": \"sent\"}",  new ObjectMapper());
        given(twilioApi.createMessage(any(), any(), anyString())).willReturn(message);


        // When
        var actual = underTest.sendSMS(phoneNumber.toString(), body);

        // Then
        assertThat(actual.isSent()).isTrue();
    }
}
