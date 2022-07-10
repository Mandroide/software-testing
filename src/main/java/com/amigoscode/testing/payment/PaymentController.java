package com.amigoscode.testing.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RequestMapping
    public void makePayment(@RequestBody PaymentRequest paymentRequest){
        paymentService.chargeCard(
                paymentRequest.getPayment().getCustomerId(),
                paymentRequest);
    }


    @RequestMapping(value = "/customer/{customerId}", method = GET)
    public List<Payment> listPayments(@PathVariable("customerId") UUID customerId){
        return paymentService.listPayments(customerId);
    }


}
