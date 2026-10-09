/*
 * ============================================================
 *  Topic 4 - Example 03 : Java is ALWAYS pass-by-value
 * ============================================================
 *  What this file demonstrates:
 *    - Passing a primitive: the method gets a COPY of the value. Changes stay inside.
 *    - Passing an array (a reference type): the method gets a COPY of the REFERENCE
 *      (the address). Both copies point to the SAME array, so changes to the CONTENTS are visible.
 *    - Re-assigning the parameter to a NEW array inside the method does NOT affect the caller.
 *    - The correct way to "change" a primitive: return the new value.
 *
 *  How to run:
 *    java Example03_PassByValue.java
 * ============================================================
 */
import java.util.Arrays;

public class Example03_PassByValue {

    // Tries to add a bonus to a primitive. Only the local COPY changes.
    static void addBonus(int salary) {
        salary = salary + 5000;
        System.out.println("  inside addBonus: salary = " + salary);
    }

    // The right way: return the new value and let the caller store it.
    static int withBonus(int salary) {
        return salary + 5000;
    }

    // Receives a copy of the reference -> points to the SAME array object -> contents change for everyone.
    static void applyDiscount(double[] prices) {
        for (int i = 0; i < prices.length; i++) {
            prices[i] = prices[i] * 0.9;     // 10% off, written into the shared array
        }
    }

    // Re-points the LOCAL copy of the reference to a brand-new array. Caller's variable is untouched.
    static void replaceArray(double[] prices) {
        prices = new double[] {1, 1, 1};
        System.out.println("  inside replaceArray: " + Arrays.toString(prices));
    }

    public static void main(String[] args) {
        // ---------- primitives ----------
        int salary = 30000;
        System.out.println("Before addBonus: salary = " + salary);
        addBonus(salary);
        System.out.println("After addBonus : salary = " + salary + "   (unchanged!)");

        salary = withBonus(salary);
        System.out.println("After withBonus: salary = " + salary + "   (changed because we stored the result)");

        // ---------- references (arrays) ----------
        double[] cartPrices = {100.0, 250.0, 40.0};
        System.out.println("Before applyDiscount: " + Arrays.toString(cartPrices));
        applyDiscount(cartPrices);
        System.out.println("After applyDiscount : " + Arrays.toString(cartPrices) + "   (contents changed!)");

        replaceArray(cartPrices);
        System.out.println("After replaceArray  : " + Arrays.toString(cartPrices) + "   (still the old array)");
    }
}

// Output:
// Before addBonus: salary = 30000
//   inside addBonus: salary = 35000
// After addBonus : salary = 30000   (unchanged!)
// After withBonus: salary = 35000   (changed because we stored the result)
// Before applyDiscount: [100.0, 250.0, 40.0]
// After applyDiscount : [90.0, 225.0, 36.0]   (contents changed!)
//   inside replaceArray: [1.0, 1.0, 1.0]
// After replaceArray  : [90.0, 225.0, 36.0]   (still the old array)
