/*
 * ============================================================
 *  Topic 3 - Example 02 : switch (classic, arrow, and switch expressions)
 * ============================================================
 *  What this file demonstrates:
 *    - Classic switch with case / break / default.
 *    - Fall-through (what happens when you forget break) - used on purpose.
 *    - Java 14+ arrow switch  case X -> ...   (no fall-through, no break needed).
 *    - Switch EXPRESSIONS that return a value, with multiple labels and yield.
 *    - Switching on a String.
 *
 *  How to run:
 *    java Example02_Switch.java
 * ============================================================
 */
public class Example02_Switch {

    public static void main(String[] args) {

        // ---------- 1. Classic switch ----------
        int dayNumber = 3;
        switch (dayNumber) {
            case 1:
                System.out.println("Monday");
                break;              // break = leave the switch now
            case 2:
                System.out.println("Tuesday");
                break;
            case 3:
                System.out.println("Wednesday");
                break;
            default:                // runs when no case matches
                System.out.println("Some other day");
        }

        // ---------- 2. Fall-through ----------
        // Without break, execution "falls through" into the next case.
        // Usually a BUG, but can be used on purpose to group cases.
        int month = 4;
        int daysInMonth;
        switch (month) {
            case 2:
                daysInMonth = 28;
                break;
            case 4:
            case 6:
            case 9:
            case 11:               // 4, 6, 9 and 11 all fall through to here
                daysInMonth = 30;
                break;
            default:
                daysInMonth = 31;
        }
        System.out.println("Month " + month + " has " + daysInMonth + " days");

        // The accidental bug, shown on purpose:
        int level = 1;
        System.out.print("Forgot break: ");
        switch (level) {
            case 1:
                System.out.print("Bronze ");   // no break...
            case 2:
                System.out.print("Silver ");   // ...so this runs too!
            case 3:
                System.out.print("Gold ");     // ...and this!
        }
        System.out.println();

        // ---------- 3. Arrow switch (Java 14+) ----------
        // case X -> ... runs ONLY that case. No fall-through, no break needed. Cleaner and safer.
        String paymentMethod = "UPI";
        switch (paymentMethod) {                       // switching on a String
            case "CARD" -> System.out.println("Redirecting to card gateway...");
            case "UPI" -> System.out.println("Showing UPI QR code...");
            case "COD" -> System.out.println("Cash on delivery selected.");
            default -> System.out.println("Unknown payment method");
        }

        // ---------- 4. Switch EXPRESSION: switch that returns a value ----------
        int monthNumber = 11;
        String season = switch (monthNumber) {
            case 12, 1, 2 -> "Winter";                 // several labels separated by commas
            case 3, 4, 5 -> "Summer";
            case 6, 7, 8, 9 -> "Monsoon";
            case 10, 11 -> "Autumn";
            default -> "Invalid month";
        };                                             // note the semicolon: it's an assignment
        System.out.println("Month " + monthNumber + " is in " + season);

        // ---------- 5. yield: when a case needs several statements ----------
        String orderStatus = "SHIPPED";
        int deliveryDays = switch (orderStatus) {
            case "PLACED" -> 5;
            case "PACKED" -> 4;
            case "SHIPPED" -> {
                System.out.println("(Order is on its way - checking courier...)");
                int courierDays = 2;
                yield courierDays;                      // yield = "the value of this case is..."
            }
            case "DELIVERED" -> 0;
            default -> -1;
        };
        System.out.println("Expected delivery in " + deliveryDays + " day(s)");

        // A switch expression must cover ALL possibilities (here via default),
        // otherwise the compiler complains - it guarantees a value is always produced.

        // ---------- 6. Simple calculator with switch on char ----------
        double x = 20, y = 8;
        char op = '*';
        double result = switch (op) {
            case '+' -> x + y;
            case '-' -> x - y;
            case '*' -> x * y;
            case '/' -> x / y;
            default -> throw new IllegalArgumentException("Unknown operator " + op);
        };
        System.out.println(x + " " + op + " " + y + " = " + result);
    }
}

// Output:
// Wednesday
// Month 4 has 30 days
// Forgot break: Bronze Silver Gold
// Showing UPI QR code...
// Month 11 is in Autumn
// (Order is on its way - checking courier...)
// Expected delivery in 2 day(s)
// 20.0 * 8.0 = 160.0
