/*
 * Example03_Enums.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   Enums - a fixed set of named constants (order statuses, days, roles...).
 *   - basic enum, values(), valueOf(), name(), ordinal()
 *   - enums with FIELDS, a CONSTRUCTOR and METHODS
 *   - switch on an enum (arrow switch, exhaustive - no default needed)
 *   - constant-specific behaviour (each constant overrides a method)
 *   - EnumMap / EnumSet
 *   - comparing enums with ==
 *
 * HOW TO RUN:
 *   java Example03_Enums.java
 */
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public class Example03_Enums {

    // ---------- 1. A basic enum ----------
    enum Day { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }

    // ---------- 2. Enum with fields, constructor and methods ----------
    enum OrderStatus {
        // Each constant calls the constructor below
        PLACED("Order placed", false),
        PACKED("Packed in warehouse", false),
        SHIPPED("On the way", false),
        DELIVERED("Delivered to customer", true),
        CANCELLED("Cancelled", true);

        private final String label;      // fields should be final - enums are constants
        private final boolean finalState;

        // Enum constructors are always private (you cannot call 'new OrderStatus(...)')
        OrderStatus(String label, boolean finalState) {
            this.label = label;
            this.finalState = finalState;
        }

        String getLabel() { return label; }
        boolean isFinalState() { return finalState; }

        // A method that uses the enum's own constants
        boolean canCancel() {
            return this == PLACED || this == PACKED;
        }
    }

    // ---------- 3. Constant-specific behaviour ----------
    enum DeliveryType {
        STANDARD {
            double charge(double orderValue) { return orderValue >= 500 ? 0 : 40; }
        },
        EXPRESS {
            double charge(double orderValue) { return 99; }
        },
        SAME_DAY {
            double charge(double orderValue) { return 149 + orderValue * 0.01; }
        };

        abstract double charge(double orderValue);   // every constant MUST implement it
    }

    public static void main(String[] args) {

        // 1. Basics
        Day today = Day.FRIDAY;
        System.out.println("1) today           : " + today);
        System.out.println("1) name()/ordinal(): " + today.name() + " / " + today.ordinal() + " (position, starts at 0)");
        System.out.println("1) valueOf         : " + Day.valueOf("SUNDAY"));
        System.out.print("1) values()        : ");
        for (Day d : Day.values()) {
            System.out.print(d.name().substring(0, 3) + " ");
        }
        System.out.println();
        try {
            Day.valueOf("Funday");
        } catch (IllegalArgumentException e) {
            System.out.println("1) bad valueOf     : " + e.getMessage());
        }

        // Compare enums with == (safe: each constant exists exactly once)
        System.out.println("1) weekend?        : " + (today == Day.SATURDAY || today == Day.SUNDAY));

        // 2. Fields and methods
        for (OrderStatus s : OrderStatus.values()) {
            System.out.printf("2) %-10s label=%-22s final=%-5b canCancel=%b%n",
                    s, s.getLabel(), s.isFinalState(), s.canCancel());
        }

        // Switch on enum (arrow switch expression). Because we cover ALL constants,
        // no 'default' is needed - the compiler checks it is EXHAUSTIVE.
        for (Day d : new Day[]{Day.MONDAY, Day.SATURDAY, Day.WEDNESDAY}) {
            String plan = switch (d) {
                case SATURDAY, SUNDAY -> "Relax";
                case MONDAY -> "Team meeting";
                case TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Write code";
            };
            System.out.println("2) " + d + " -> " + plan);
        }

        // 3. Constant-specific behaviour (no if/else chain needed!)
        double order = 450;
        for (DeliveryType t : DeliveryType.values()) {
            System.out.println("3) " + t + " charge for Rs." + order + " = " + t.charge(order));
        }

        // 4. EnumMap: a fast Map whose keys are enum constants (kept in declaration order)
        Map<OrderStatus, Integer> countByStatus = new EnumMap<>(OrderStatus.class);
        countByStatus.put(OrderStatus.SHIPPED, 12);
        countByStatus.put(OrderStatus.PLACED, 5);
        countByStatus.put(OrderStatus.DELIVERED, 40);
        System.out.println("4) EnumMap         : " + countByStatus);

        // EnumSet: a fast Set of enum constants
        EnumSet<Day> workDays = EnumSet.range(Day.MONDAY, Day.FRIDAY);
        EnumSet<Day> weekend = EnumSet.complementOf(workDays);
        System.out.println("4) work days       : " + workDays);
        System.out.println("4) weekend         : " + weekend);
    }
}

/* Output:
1) today           : FRIDAY
1) name()/ordinal(): FRIDAY / 4 (position, starts at 0)
1) valueOf         : SUNDAY
1) values()        : MON TUE WED THU FRI SAT SUN
1) bad valueOf     : No enum constant Example03_Enums.Day.Funday
1) weekend?        : false
2) PLACED     label=Order placed           final=false canCancel=true
2) PACKED     label=Packed in warehouse    final=false canCancel=true
2) SHIPPED    label=On the way             final=false canCancel=false
2) DELIVERED  label=Delivered to customer  final=true  canCancel=false
2) CANCELLED  label=Cancelled              final=true  canCancel=false
2) MONDAY -> Team meeting
2) SATURDAY -> Relax
2) WEDNESDAY -> Write code
3) STANDARD charge for Rs.450.0 = 40.0
3) EXPRESS charge for Rs.450.0 = 99.0
3) SAME_DAY charge for Rs.450.0 = 153.5
4) EnumMap         : {PLACED=5, SHIPPED=12, DELIVERED=40}
4) work days       : [MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY]
4) weekend         : [SATURDAY, SUNDAY]
*/
