package com.amigoscode.testing.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerRegistrationService {
    private final CustomerRepository repository;

    @Autowired
    public CustomerRegistrationService(CustomerRepository repository) {
        this.repository = repository;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        Optional<Customer> optional = repository.findCustomerByPhoneNumber(
                request.getCustomer().getPhoneNumber()
        );
        if (optional.isPresent()){
            Customer customer = optional.get();
            if(!customer.equals(request.getCustomer())){
                throw new IllegalArgumentException("A customer with "
                        + customer.getPhoneNumber() + " already exists.");
            }
        } else {
            repository.save(request.getCustomer());
        }
    }
}
