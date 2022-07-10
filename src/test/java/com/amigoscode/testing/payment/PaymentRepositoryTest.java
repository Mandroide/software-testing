package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

        @Test
    void findPaymentsByCustomerId_WhenCustomerHavePayments_ReturnsCustomerPayments() {
        // Given
        long paymentId = 1L;
        UUID customerId = UUID.randomUUID();
        Payment payment = new Payment(paymentId, customerId,
                new BigDecimal(1), Currency.USD, "source",
                "description");

        underTest.save(payment);

        // When
        List<Payment> paymentsByCustomerId
                = underTest.findPaymentsByCustomerId(customerId);

        // Then
        assertThat(paymentsByCustomerId.size()).isEqualTo(1);
        assertThat(paymentsByCustomerId.get(0)).isEqualTo(payment);
    }

    @Test
    void findPaymentsByCustomerId_WhenCustomerDoesNotExist_NoPaymentsFound() {
        // Given
        UUID customerId = UUID.randomUUID();

        // When
        List<Payment> payments = underTest.findPaymentsByCustomerId(customerId);

        assertThat(payments.isEmpty()).isTrue();
    }
}