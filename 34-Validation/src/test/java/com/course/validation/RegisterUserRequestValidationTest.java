package com.course.validation;

import com.course.validation.dto.AddressRequest;
import com.course.validation.dto.RegisterUserRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Testing constraints WITHOUT Spring: we ask Hibernate Validator directly.
 * This is fast (milliseconds) and is the best way to test custom validators.
 */
class RegisterUserRequestValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void close() {
        factory.close();
    }

    @Test
    void validRequestHasNoViolations() {
        assertThat(validator.validate(validRequest("Secret@123", "9876543210"))).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"short1!", "alllowercase1!", "ALLUPPERCASE1!", "NoDigits!!", "NoSpecial123"})
    void weakPasswordsAreRejected(String weakPassword) {
        Set<String> fields = invalidFields(validRequest(weakPassword, null));
        assertThat(fields).containsExactly("password");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "0987654321", "98765-43210", "+9198765"})
    void badPhoneNumbersAreRejected(String badPhone) {
        assertThat(invalidFields(validRequest("Secret@123", badPhone))).containsExactly("phone");
    }

    @Test
    void nestedAddressIsValidatedBecauseOfValid() {
        RegisterUserRequest request = new RegisterUserRequest("Asha", "asha@example.com",
                "Secret@123", null, 25, LocalDate.of(2000, 1, 1),
                new AddressRequest("", "Pune", "12"), List.of("java"));

        assertThat(invalidFields(request)).containsExactlyInAnyOrder("address.street", "address.pinCode");
    }

    private Set<String> invalidFields(RegisterUserRequest request) {
        return validator.validate(request).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    private RegisterUserRequest validRequest(String password, String phone) {
        return new RegisterUserRequest("Asha Rao", "asha@example.com", password, phone, 25,
                LocalDate.of(2000, 5, 21), new AddressRequest("MG Road 1", "Bengaluru", "560001"),
                List.of("java", "music"));
    }
}
