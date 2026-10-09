package com.course.patterns;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.course.patterns.patterns.strategy.PaymentService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/*
 * Starts the Spring context (WITHOUT running the console demos) and checks the
 * Spring-based patterns: Strategy beans collected into a Map by bean name.
 */
@SpringBootTest(properties = "demo.runner.enabled=false")
class PatternsSpringTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void allStrategiesAreRegisteredByBeanName() {
        assertThat(paymentService.availableMethods()).containsExactlyInAnyOrder("upi", "card", "wallet");
    }

    @Test
    void cardPaymentAddsTwoPercentFee() {
        assertThat(paymentService.checkout("card", new BigDecimal("500.00")))
                .isEqualTo("Card payment of 500.00 + fee 10.00");
    }

    @Test
    void unknownMethodIsRejected() {
        assertThatThrownBy(() -> paymentService.checkout("cash", BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cash");
    }
}
