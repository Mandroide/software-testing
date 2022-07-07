package com.amigoscode.testing.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerRegistrationService {
    private final CustomerRepository repository;

    @Autowired
    public CustomerRegistrationService(CustomerRepository repository) {
        this.repository = repository;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {
    }
}
