/*
 * Example05_SwitchAndPatternMatching.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   PART A - Switch expressions (Java 14):
 *            arrow labels (no fall-through, no break), several labels per case,
 *            switch that RETURNS a value, 'yield' in a block, exhaustiveness.
 *   PART B - Pattern matching for instanceof (Java 16):
 *            test + cast + new variable in one step, combined with &&.
 *            Also an equals() method written the modern way.
 *
 * HOW TO RUN:
 *   java Example05_SwitchAndPatternMatching.java
 */
import java.util.List;

public class Example05_SwitchAndPatternMatching {

    enum Size { SMALL, MEDIUM, LARGE }

    public static void main(String[] args) {

        // ================= PART A: switch =================

        // Old switch STATEMENT - easy to forget 'break' (fall-through bug)
        int day = 6;
        String typeOld;
        switch (day) {
            case 1:
            case 7:
                typeOld = "Weekend";
                break;
            default:
                typeOld = "Weekday";
                break;
        }

        // New switch EXPRESSION - returns a value, no break, no fall-through
        String typeNew = switch (day) {
            case 1, 7 -> "Weekend";
            default -> "Weekday";
        };
        System.out.println("A) day 6: old=" + typeOld + ", new=" + typeNew);

        // switch on String with several labels
        String city = "Chennai";
        int deliveryDays = switch (city) {
            case "Chennai", "Bengaluru", "Hyderabad" -> 1;
            case "Delhi", "Mumbai" -> 2;
            default -> 5;
        };
        System.out.println("A) delivery to " + city + ": " + deliveryDays + " day(s)");

        // A block with 'yield' when you need more than one statement
        double orderValue = 2500;
        double discount = switch (city) {
            case "Chennai" -> {
                double base = orderValue * 0.05;
                yield Math.min(base, 100);          // 'yield' gives the value of the block
            }
            case "Delhi" -> 50;
            default -> 0;
        };
        System.out.println("A) discount: " + discount);

        // On an enum: covering every constant = exhaustive, so no default needed.
        // If someone adds Size.XL later, this stops compiling -> you can't forget it!
        for (Size s : Size.values()) {
            int price = switch (s) {
                case SMALL -> 99;
                case MEDIUM -> 149;
                case LARGE -> 199;
            };
            System.out.println("A) pizza " + s + " = Rs." + price);
        }

        // The arrow form also works as a plain statement (no value)
        int statusCode = 404;
        switch (statusCode) {
            case 200, 201 -> System.out.println("A) Success");
            case 400 -> System.out.println("A) Bad request");
            case 404 -> System.out.println("A) Not found");
            default -> System.out.println("A) Other: " + statusCode);
        }

        // ================= PART B: pattern matching for instanceof =================
        List<Object> things = List.of("Spring Boot", 42, 3.5, List.of(1, 2, 3), "", -7);

        for (Object obj : things) {
            System.out.println("B) " + describe(obj));
        }

        Money a = new Money(100, "INR");
        Money b = new Money(100, "INR");
        System.out.println("B) equals with pattern: " + a.equals(b) + ", vs String: " + a.equals("100 INR"));
    }

    static String describe(Object obj) {
        // OLD:  if (obj instanceof String) { String s = (String) obj; ... }
        // NEW:  test, cast and declare 's' in one go
        if (obj instanceof String s && !s.isEmpty()) {   // 's' can be used after &&
            return "String of length " + s.length() + ": " + s.toUpperCase();
        } else if (obj instanceof String) {
            return "an empty String";
        } else if (obj instanceof Integer i && i > 0) {
            return "positive Integer, doubled = " + (i * 2);
        } else if (obj instanceof Integer i) {
            return "non-positive Integer " + i;
        } else if (obj instanceof Double d) {
            return "Double, rounded = " + Math.round(d);
        } else if (obj instanceof List<?> list) {
            return "List with " + list.size() + " elements";
        }
        return "something else";
        // Newer Java (21+) adds "pattern matching for switch":
        //   return switch (obj) { case String s -> ...; case Integer i -> ...; default -> ...; };
        // In Java 17 it is only a preview feature, so we use if/else here.
    }

    // A classic place for instanceof patterns: equals()
    static final class Money {
        private final int amount;
        private final String currency;

        Money(int amount, String currency) {
            this.amount = amount;
            this.currency = currency;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Money other
                    && amount == other.amount
                    && currency.equals(other.currency);
        }

        @Override
        public int hashCode() {
            return 31 * amount + currency.hashCode();
        }
    }
}

/* Output:
A) day 6: old=Weekday, new=Weekday
A) delivery to Chennai: 1 day(s)
A) discount: 100.0
A) pizza SMALL = Rs.99
A) pizza MEDIUM = Rs.149
A) pizza LARGE = Rs.199
A) Not found
B) String of length 11: SPRING BOOT
B) positive Integer, doubled = 84
B) Double, rounded = 4
B) List with 3 elements
B) an empty String
B) non-positive Integer -7
B) equals with pattern: true, vs String: false
*/
