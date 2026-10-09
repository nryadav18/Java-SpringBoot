package com.course.configprops;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/*
 * Checks that application.yml + application-dev.yml were bound into ShopProperties.
 * "dev" is active because application.yml sets spring.profiles.active=dev.
 * Run with: mvn test
 */
@SpringBootTest
class ConfigPropsApplicationTests {

    @Autowired
    private ShopProperties shop;

    @Test
    void devProfileOverridesTheBaseFile() {
        assertThat(shop.name()).isEqualTo("ShopEasy (DEV)");   // from application-dev.yml
        assertThat(shop.discountPercent()).isEqualTo(20);      // from application-dev.yml
    }

    @Test
    void baseValuesAreStillThere() {
        assertThat(shop.currency()).isEqualTo("INR");          // only in application.yml
        assertThat(shop.sessionTimeout()).isEqualTo(Duration.ofMinutes(30));
        assertThat(shop.supportedPaymentMethods()).containsExactly("UPI", "CARD", "CASH_ON_DELIVERY");
        assertThat(shop.contact().email()).isEqualTo("support@shopeasy.example");
    }
}
