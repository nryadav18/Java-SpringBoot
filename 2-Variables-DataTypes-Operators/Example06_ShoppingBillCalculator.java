/*
 * ============================================================
 *  Topic 2 - Example 06 : Mini-program - Shopping Bill Calculator
 * ============================================================
 *  What this file demonstrates:
 *    - Using variables, constants, arithmetic and printf together in a realistic task.
 *    - Calculating subtotal, discount, GST and the final amount.
 *
 *  How to run:
 *    java Example06_ShoppingBillCalculator.java
 * ============================================================
 */
public class Example06_ShoppingBillCalculator {

    // Constants: values that never change while the program runs.
    static final double GST_RATE = 0.18;          // 18% tax
    static final double DISCOUNT_RATE = 0.10;     // 10% discount
    static final double DISCOUNT_THRESHOLD = 1000; // discount only if subtotal >= 1000

    public static void main(String[] args) {

        // Items in the cart (name, unit price, quantity)
        String item1 = "Notebook";   double price1 = 60.0;   int qty1 = 5;
        String item2 = "Pen";        double price2 = 15.5;   int qty2 = 10;
        String item3 = "Backpack";   double price3 = 799.0;  int qty3 = 1;

        // Line totals = price x quantity
        double line1 = price1 * qty1;
        double line2 = price2 * qty2;
        double line3 = price3 * qty3;

        double subtotal = line1 + line2 + line3;
        int totalItems = qty1 + qty2 + qty3;

        // Discount: a boolean decides, and we multiply by 1 or 0 (no if-statement yet - that's Topic 3!)
        boolean eligibleForDiscount = subtotal >= DISCOUNT_THRESHOLD;
        double discount = eligibleForDiscount ? subtotal * DISCOUNT_RATE : 0;
        // The ?: above is the "ternary operator": condition ? valueIfTrue : valueIfFalse (more in Topic 3).

        double afterDiscount = subtotal - discount;
        double gst = afterDiscount * GST_RATE;
        double grandTotal = afterDiscount + gst;

        // %-10s = text, left-aligned in 10 spaces; %8.2f = number in 8 spaces with 2 decimals
        System.out.println("=========== BILL ===========");
        System.out.printf("%-10s %3d x %7.2f = %8.2f%n", item1, qty1, price1, line1);
        System.out.printf("%-10s %3d x %7.2f = %8.2f%n", item2, qty2, price2, line2);
        System.out.printf("%-10s %3d x %7.2f = %8.2f%n", item3, qty3, price3, line3);
        System.out.println("----------------------------");
        System.out.printf("Items: %d%n", totalItems);
        System.out.printf("Subtotal         : %9.2f%n", subtotal);
        System.out.printf("Discount (10%%)   : %9.2f%n", discount);   // %% prints a single %
        System.out.printf("After discount   : %9.2f%n", afterDiscount);
        System.out.printf("GST (18%%)        : %9.2f%n", gst);
        System.out.println("----------------------------");
        System.out.printf("GRAND TOTAL      : %9.2f%n", grandTotal);
        System.out.printf("Rounded to rupee : %9d%n", Math.round(grandTotal));
    }
}

// Output:
// =========== BILL ===========
// Notebook     5 x   60.00 =   300.00
// Pen         10 x   15.50 =   155.00
// Backpack     1 x  799.00 =   799.00
// ----------------------------
// Items: 16
// Subtotal         :   1254.00
// Discount (10%)   :    125.40
// After discount   :   1128.60
// GST (18%)        :    203.15
// ----------------------------
// GRAND TOTAL      :   1331.75
// Rounded to rupee :      1332
