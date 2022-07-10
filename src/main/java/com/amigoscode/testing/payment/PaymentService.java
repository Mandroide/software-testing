package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private static final List<Currency> ACCEPTED_CURRENCIES
            = List.of(Currency.USD, Currency.GBP);
    private final CustomerRepository customerRepository;
    private final PaymentRepository repository;
    private final CardPaymentCharger cardPaymentCharger;


    @Autowired
    public PaymentService(CustomerRepository customerRepository, PaymentRepository repository, CardPaymentCharger cardPaymentCharger) {
        this.customerRepository = customerRepository;
        this.repository = repository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    List<Payment> listPayments(UUID customerId){
        return repository.findPaymentsByCustomerId(customerId);
    }

    void chargeCard(UUID customerId, PaymentRequest request){
        // 1. Does customer exists if not throw
        // 2. Do we support the currency if not throw
        // 3. Charge card
        // 4. If not debited throw
        // 5. Insert Payment
        // 6. TODO: send sms
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        Payment payment = request.getPayment();
        String msg = "";
        if (optionalCustomer.isPresent()){
            boolean isCurrencySupported = ACCEPTED_CURRENCIES.contains(request.getPayment().getCurrency());
            if (isCurrencySupported){
                CardPaymentCharge paymentCharge
                        = cardPaymentCharger.chargeCard(payment.getSource(),
                        payment.getAmount(), payment.getCurrency(),
                        payment.getDescription());
                if (paymentCharge.isCardDebited()){
                    request.getPayment().setCustomerId(customerId);
                    repository.save(payment);
                } else {
                    msg = String.format(
                            "Card no debited for %s %s",
                            Customer.class.getSimpleName().toLowerCase(),
                            customerId
                    );
                }
            } else {
                msg = String.format("%s[%s] not supported.",
                        Currency.class.getSimpleName(),
                        request.getPayment().getCurrency()
                );
            }

        } else {
            msg = String.format("%s with id %s not found.",
                    Customer.class.getSimpleName(), customerId);
        }

        if(!msg.isEmpty()) {
            throw new IllegalArgumentException(
                    msg
            );
        }

    }
}
