package com.amigoscode.testing.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator underTest;

    @BeforeEach
    void setUp() {
        underTest = new PhoneNumberValidator();
    }

    @ParameterizedTest
    @CsvSource({
            "+447000000000, true",
            "+447000000088878, false",
            "4470000000, false"})
    void validatePhoneNumber_WhenCalled_ReturnsBoolean(String phoneNumber, boolean expected) {
        // When
        boolean actual = underTest.test(phoneNumber);

        // Then
        assertThat(actual).isEqualTo(expected);
    }
}
