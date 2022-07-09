package com.amigoscode.testing.payment.stripe;

import com.amigoscode.testing.payment.CardPaymentCharge;
import com.amigoscode.testing.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;


class StripeServiceTest {
    @Mock
    private StripeApi stripeApi;
    private StripeService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new StripeService(stripeApi);
    }

    @Test
    void chargeCard_ArgumentsAreValid_ChargedSuccessfully() throws StripeException {
        // Given
        String cardSource = "cardSource";
        BigDecimal amount = new BigDecimal(1);
        Currency currency = Currency.USD;
        String description = "description";
        Charge charge = new Charge();
        charge.setPaid(true);
        BDDMockito.given(stripeApi.createCharge(ArgumentMatchers.anyMap(),
                any())).willReturn(charge);

        // When
        CardPaymentCharge actual = underTest.chargeCard(cardSource, amount,
                currency, description);

        // Then
        // Capture arguments for stripeApi.createCharge()
        ArgumentCaptor<Map<String, Object>> mapArgumentCaptor
                = ArgumentCaptor.forClass(Map.class);
        var requestOptionsArgumentCaptor
                = ArgumentCaptor.forClass(RequestOptions.class);

        BDDMockito.then(stripeApi).should().createCharge(
                mapArgumentCaptor.capture(),
                requestOptionsArgumentCaptor.capture());

        // Assert that the Map<String, Object> parameter has this values.
        var requestMap = mapArgumentCaptor.getValue();
        Assertions.assertThat(requestMap.keySet()).hasSize(4);
        assertThat(requestMap.get("amount")).isEqualTo(amount);
        assertThat(requestMap.get("currency")).isEqualTo(currency);
        assertThat(requestMap.get("source")).isEqualTo(cardSource);
        assertThat(requestMap.get("description")).isEqualTo(description);

        // Assert that the RequestOptions parameter is not null.
        RequestOptions requestOptions = requestOptionsArgumentCaptor.getValue();
        assertThat(requestOptions).isNotNull();

        // Assert that the CardPaymentCharge is not null and a debited car happened.
        assertThat(actual).isNotNull();
        assertThat(actual.isCardDebited()).isTrue();
    }

    @Test
    void chargeCard_ArgumentsAreNotValid_ChargedFailed() throws StripeException {
        // Given
        String cardSource = "cardSource";
        BigDecimal amount = new BigDecimal(1);
        Currency currency = Currency.USD;
        String description = "description";
        Charge charge = new Charge();
        charge.setPaid(true);

        BDDMockito.doThrow(new StripeException("message", "requestId", "code", 0) {
        }).when(stripeApi).createCharge(anyMap(), any());

        // When
        // Then
        assertThatExceptionOfType(Exception.class).isThrownBy(
                () -> underTest.chargeCard(cardSource, amount,
                        currency, description))
                .withMessageContaining("Cannot make stripe charge");

    }
}