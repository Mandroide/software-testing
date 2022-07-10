package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.MimeTypeUtils;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createPaymentSucessfully() throws Exception {
        // Given a customer
        UUID customerId = UUID.randomUUID();
        String phoneNumber = "+447000000000";
        Customer customer = new Customer(customerId, "name", phoneNumber);
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);
        // When register
        ResultActions customerRegResultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/customer-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(
                        customerRegistrationRequest))));
        // ... Payment
        long paymentId = 1L;
        Payment payment = new Payment(paymentId, customerId, new BigDecimal(1),
                Currency.USD, "source", "description");
        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        // When payment is sent
        ResultActions paymentResultActions = mockMvc.perform(post("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(paymentRequest))));

        // Then both customer and registration and payment requests are 200 status
        customerRegResultActions.andExpect(status().isOk());
        paymentResultActions.andExpect(status().isOk());
        // Payment is stored in DB
        ResultActions resultActions = mockMvc.perform(get("/api/v1/payment/customer/".concat(customerId.toString()))
                .accept(MimeTypeUtils.APPLICATION_JSON_VALUE));

        resultActions.andExpect(status().isOk());
        // TODO: Ensure SMS is delivered
    }

    private String objectToJson(Object obj) {
        String result;
        try{
            result = new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            fail("Failed to convert object to JSON");
            result = null;
        }
        return result;
    }
}
