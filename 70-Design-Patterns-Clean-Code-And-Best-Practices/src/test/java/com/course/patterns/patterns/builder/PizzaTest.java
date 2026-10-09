package com.course.patterns.patterns.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/* Plain unit tests for the Builder: valid objects, validation, immutability. */
class PizzaTest {

    @Test
    void buildsPizzaStepByStep() {
        Pizza pizza = Pizza.builder("MEDIUM").topping("corn").topping("onion").build();

        assertThat(pizza.size()).isEqualTo("MEDIUM");
        assertThat(pizza.toppings()).containsExactly("corn", "onion");
    }

    @Test
    void rejectsUnknownSize() {
        assertThatThrownBy(() -> Pizza.builder("HUGE").build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("HUGE");
    }

    @Test
    void rejectsTooManyToppings() {
        Pizza.Builder builder = Pizza.builder("LARGE");
        for (int i = 1; i <= 6; i++) {
            builder.topping("t" + i);
        }
        assertThatThrownBy(builder::build).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void toppingsCannotBeChangedAfterBuild() {
        Pizza pizza = Pizza.builder("SMALL").topping("paneer").build();

        assertThatThrownBy(() -> pizza.toppings().add("pineapple"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
