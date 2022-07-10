package com.amigoscode.testing.utils;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class PhoneNumberValidator implements Predicate<String> {

    @Override
    public boolean test(String phoneNumber) {
        return phoneNumber.startsWith("+44")
                && phoneNumber.length() == 13;
    }

    @Override
    public Predicate<String> and(Predicate<? super String> other) {
        return Predicate.super.and(other);
    }

    @Override
    public Predicate<String> negate() {
        return Predicate.super.negate();
    }

    @Override
    public Predicate<String> or(Predicate<? super String> other) {
        return Predicate.super.or(other);
    }
}
