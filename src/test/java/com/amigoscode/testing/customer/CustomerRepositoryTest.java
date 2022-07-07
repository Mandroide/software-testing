package com.amigoscode.testing.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void findCustomerByPhoneNumber_WhenNumberDoesNotExist_NoCustomerFound() {
        // Given
        String phoneNumber = "0000";

        // When
        Optional<Customer> customerByPhoneNumber = underTest.findCustomerByPhoneNumber(phoneNumber);

        assertThat(customerByPhoneNumber).isNotPresent();
    }

    @Test
    void findCustomerByPhoneNumber_WhenNumberExists_CustomerFound() {
        // Given
        String phoneNumber = "0000";
        Customer customer = new Customer(UUID.randomUUID(), "Abel", phoneNumber);
        underTest.save(customer);

        // When
        Optional<Customer> customerOptional = underTest.findCustomerByPhoneNumber(phoneNumber);

        // Then
        assertThat(customerOptional)
                .isPresent()
                .hasValueSatisfying(c -> assertThat(c).isEqualToComparingFieldByField(customer));
    }
}