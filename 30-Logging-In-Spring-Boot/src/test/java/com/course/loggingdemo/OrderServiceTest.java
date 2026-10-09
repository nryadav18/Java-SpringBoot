package com.course.loggingdemo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

/*
 * Plain unit test (no Spring context) that also checks WHAT gets logged.
 * OutputCaptureExtension (from spring-boot-test) captures everything written to the console.
 * Run with: mvn test
 */
@ExtendWith(OutputCaptureExtension.class)
class OrderServiceTest {

    private final OrderService service = new OrderService();

    @Test
    void placingAnOrderLogsAnInfoMessage(CapturedOutput output) {
        Order order = service.placeOrder("Asha", "Pizza", 2, "4111111111111111");

        assertThat(order.status()).isEqualTo("PLACED");
        assertThat(output).contains("Order " + order.id() + " placed: 2 x Pizza for customer Asha");
    }

    @Test
    void theFullCardNumberIsNeverLogged(CapturedOutput output) {
        service.placeOrder("Ravi", "Biryani", 1, "4111111111111111");

        assertThat(output).doesNotContain("4111111111111111");
    }

    @Test
    void invalidQuantityLogsAWarning(CapturedOutput output) {
        assertThatThrownBy(() -> service.placeOrder("Asha", "Pizza", 99, "4111111111111111"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(output).contains("WARN").contains("invalid quantity 99");
    }

    @Test
    void maskCardKeepsOnlyLastFourDigits() {
        assertThat(OrderService.maskCard("4111111111111111")).isEqualTo("************1111");
        assertThat(OrderService.maskCard(null)).isEqualTo("****");
    }
}
