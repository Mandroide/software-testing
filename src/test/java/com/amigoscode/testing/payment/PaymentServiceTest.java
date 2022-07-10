package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class PaymentServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;
    @Mock
    private SMSSender smsSender;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(customerRepository,
                paymentRepository,
                cardPaymentCharger,
                smsSender);
    }

    @Test
    void chargeCard_CustomerExistsAndCurrencySupportedAndCardIsDebited_PaymentSuccessful() {
        // Given
        UUID customerId = UUID.randomUUID();
        // Customer Exists
        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(mock(Customer.class)));
        // Payment request
        PaymentRequest request = new PaymentRequest(new Payment(
                null, null, new BigDecimal("1.00"), Currency.USD,
                "source","description"));
        // ... Card is charged successfully
        given(cardPaymentCharger.chargeCard(
                request.getPayment().getSource(),
                request.getPayment().getAmount(),
                request.getPayment().getCurrency(),
                request.getPayment().getDescription()
                )).willReturn(new CardPaymentCharge(true));

        // ... SMS is delivered successfully
        given(smsSender.sendSMS(any(), any())).willReturn(new SMSSent(true));

        // When
        underTest.chargeCard(customerId, request);

        // Then
        ArgumentCaptor<Payment> paymentArgumentCaptor
                = ArgumentCaptor.forClass(Payment.class);
        then(paymentRepository).should().save(paymentArgumentCaptor.capture());

        Payment captorValue = paymentArgumentCaptor.getValue();
        assertThat(captorValue).isEqualToIgnoringGivenFields(request.getPayment(), "customerId");
        assertThat(captorValue.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void chargeCard_CardIsNotDebited_ThrowException() {
        // Given
        UUID customerId = UUID.randomUUID();
        // Customer Exists
        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(mock(Customer.class)));
        // Payment request
        PaymentRequest request = new PaymentRequest(new Payment(
                null, null, new BigDecimal("1.00"), Currency.USD,
                "source","description"));

        // Card is not charged successfully
        given(cardPaymentCharger.chargeCard(
                request.getPayment().getSource(),
                request.getPayment().getAmount(),
                request.getPayment().getCurrency(),
                request.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(false));

        // When
        // Then
        assertThatExceptionOfType(Exception.class).isThrownBy(
                ()-> underTest.chargeCard(customerId, request))
                .withMessageContaining(String.format(
                        "Card no debited for %s %s",
                        Customer.class.getSimpleName().toLowerCase(),
                        customerId
                ));
        then(paymentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void chargeCard_CurrencyNotSupported_ThrowException() {
        // Given
        UUID customerId = UUID.randomUUID();
        // Customer Exists
        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(mock(Customer.class)));
        // Payment request
        Currency currency = Currency.EUR;
        PaymentRequest request = new PaymentRequest(new Payment(
                null, null, new BigDecimal("1.00"), currency,
                "source","description"));

        // When
        assertThatExceptionOfType(Exception.class).isThrownBy(
                        ()-> underTest.chargeCard(customerId, request))
                .withMessageContaining(String.format("%s[%s] not supported.",
                        Currency.class.getSimpleName(),
                        currency
                ));
        // Then
        then(cardPaymentCharger).shouldHaveNoMoreInteractions();
        then(paymentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void chargeCard_CustomerNotFound_ThrowException() {
        // Given
        UUID customerId = UUID.randomUUID();
        // Customer Exists
        given(customerRepository.findById(customerId))
                .willReturn(Optional.empty());
        // When
        // Then
        assertThatExceptionOfType(Exception.class).isThrownBy(
                () -> underTest.chargeCard(customerId,
                        new PaymentRequest(new Payment())))
                        .withMessageContaining(String.format("%s with id %s not found.",
                                Customer.class.getSimpleName(), customerId));
        then(cardPaymentCharger).shouldHaveNoMoreInteractions();
        then(paymentRepository).shouldHaveNoMoreInteractions();

    }
}