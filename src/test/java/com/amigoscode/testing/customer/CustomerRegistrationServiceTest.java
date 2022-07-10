package com.amigoscode.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class CustomerRegistrationServiceTest {
    @Mock
    private CustomerRepository repository;
    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;
    private CustomerRegistrationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.underTest = new CustomerRegistrationService(repository);
    }

    @Test
    void registerNewCustomer_RequestPhoneNumberDoesNotExist_AddCustomer() {
        // Given
        Customer customer = new Customer(UUID.randomUUID(), "name", "phone");
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        // No customer with phoneNumber found.
        given(repository.findCustomerByPhoneNumber(request.getCustomer()
                .getPhoneNumber())).willReturn(Optional.empty());

        // When
        underTest.registerNewCustomer(request);
        // Then
        then(repository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualTo(customer);
    }

    @Test
    void registerNewCustomer_RequestPhoneNumberExistsForSameCustomer_DoNothing() {
        // Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "phone";
        Customer customer = new Customer(id, "name", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        // Same customer with phoneNumber found.
        given(repository.findCustomerByPhoneNumber(request.getCustomer()
                .getPhoneNumber())).willReturn(Optional.of(customer));

        // When
        underTest.registerNewCustomer(request);
        // Then
//        then(repository).should(never()).save(any());
        then(repository).should().findCustomerByPhoneNumber(phoneNumber);
        then(repository).shouldHaveNoMoreInteractions();
    }

    @Test
    void registerNewCustomer_RequestPhoneNumberExistsForAnotherCustomer_ThrowsException() {
        // Given
        String phoneNumber = "phone";
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                new Customer(UUID.randomUUID(), "name", phoneNumber)
        );
        // Another customer with phoneNumber found.
        given(repository.findCustomerByPhoneNumber(phoneNumber)).willReturn(
                Optional.of(new Customer(
                        UUID.randomUUID(),"name", phoneNumber)));


        // When and Then
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> underTest.registerNewCustomer(request));
        then(repository).should().findCustomerByPhoneNumber(phoneNumber);
        then(repository).shouldHaveNoMoreInteractions();
    }

}