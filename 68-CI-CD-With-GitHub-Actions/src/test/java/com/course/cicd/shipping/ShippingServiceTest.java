package com.course.cicd.shipping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/*
 * ShippingServiceTest.java - fast UNIT tests (no Spring, no web server).
 * "mvn -B verify" in the CI pipeline runs these on every push and pull request.
 *
 * Try it: change PRICE_PER_KG to 3.00 in ShippingService and run "mvn test" -> RED.
 * On GitHub, the same failure would block the pull request.
 */
class ShippingServiceTest {

    private final ShippingService service = new ShippingService();

    @Test
    @DisplayName("Standard delivery: 5.00 base + 2.00 per kg")
    void standardDelivery() {
        ShippingQuote quote = service.quote(2.5, false);

        // 5.00 + 2.5 * 2.00 = 10.00
        assertThat(quote.cost()).isEqualByComparingTo("10.00");
        assertThat(quote.express()).isFalse();
    }

    @Test
    @DisplayName("Express delivery costs 1.5 times the standard price")
    void expressDelivery() {
        ShippingQuote quote = service.quote(2.5, true);

        // 10.00 * 1.5 = 15.00
        assertThat(quote.cost()).isEqualByComparingTo(new BigDecimal("15.00"));
    }

    @ParameterizedTest(name = "{0} kg, express={1} -> {2}")
    @CsvSource({
            "1,    false, 7.00",
            "10,   false, 25.00",
            "30,   false, 65.00",
            "0.5,  true,  9.00"
    })
    void manyWeights(double weightKg, boolean express, String expectedCost) {
        assertThat(service.quote(weightKg, express).cost()).isEqualByComparingTo(expectedCost);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -1, 30.01, 100})
    @DisplayName("Weights outside (0, 30] are rejected")
    void invalidWeights(double weightKg) {
        assertThatThrownBy(() -> service.quote(weightKg, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Weight must be");
    }
}
