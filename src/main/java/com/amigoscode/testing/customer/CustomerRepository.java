package com.amigoscode.testing.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    @Query(value = "select * from \"customer\" where \"phone_number\" = :phone_number",
            nativeQuery = true)
    Optional<Customer> findCustomerByPhoneNumber(@Param("phone_number") String phoneNumber);

}
