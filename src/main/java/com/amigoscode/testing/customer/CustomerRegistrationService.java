package com.amigoscode.testing.customer;

import com.amigoscode.testing.utils.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerRegistrationService {
    private final CustomerRepository repository;
    private final PhoneNumberValidator phoneNumberValidator;

    @Autowired
    public CustomerRegistrationService(CustomerRepository repository, PhoneNumberValidator phoneNumberValidator) {
        this.repository = repository;
        this.phoneNumberValidator = phoneNumberValidator;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        String phoneNumber = request.getCustomer().getPhoneNumber();

        if (phoneNumberValidator.test(phoneNumber)){
            Optional<Customer> optional = repository.findCustomerByPhoneNumber(
                    phoneNumber
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

        } else {
            throw new IllegalArgumentException("Phone Number " + phoneNumber + " is not valid.");
        }

    }
}
