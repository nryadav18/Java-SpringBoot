package com.course.patterns.cleancode;

import static org.assertj.core.api.Assertions.assertThat;

import com.course.patterns.cleancode.CleanCodeDemo.After;
import com.course.patterns.cleancode.CleanCodeDemo.After.CustomerTier;
import com.course.patterns.cleancode.CleanCodeDemo.After.Delivery;
import com.course.patterns.cleancode.CleanCodeDemo.Before;
import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/*
 * A refactoring must NOT change behaviour. These tests prove that the "after" version
 * gives the same answers as the "before" version - this safety net is what makes
 * refactoring safe (write tests first, then clean up).
 */
class CleanCodeTest {

    @ParameterizedTest(name = "amount {0}, oldType {1}, tier {2}, express {3}")
    @CsvSource({
            "1000, 0, REGULAR, false",
            "1000, 1, SILVER,  true",
            "1000, 2, GOLD,    true",
            "250,  2, GOLD,    false",
            "99.5, 1, SILVER,  false"
    })
    void refactoringKeepsBehaviour(String amount, int oldType, CustomerTier tier, boolean express) {
        double before = Before.calc(oldType, Double.parseDouble(amount), express);
        BigDecimal after = After.finalPrice(new BigDecimal(amount), tier,
                express ? Delivery.EXPRESS : Delivery.STANDARD);

        assertThat(after).isEqualByComparingTo(BigDecimal.valueOf(before).setScale(2, java.math.RoundingMode.HALF_UP));
    }
}
