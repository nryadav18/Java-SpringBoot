/*
 * Example04_BoundedTypes.java
 *
 * What this file demonstrates:
 *   - Plain <T> knows NOTHING about T, so you can only call Object methods on it.
 *   - A BOUNDED type parameter restricts T and, in return, lets you USE T's methods:
 *       <T extends Number>              -> T is Number or a subclass (Integer, Double, ...)
 *                                          so we can call doubleValue()
 *       <T extends Comparable<T>>       -> T can be compared, so we can find a maximum
 *       <T extends Payable & Comparable<T>>  -> MULTIPLE bounds (a class first, then interfaces, joined with &)
 *   - "extends" in a bound means "extends OR implements".
 *   - A bounded generic CLASS: Statistics<T extends Number>
 *
 * Domain: shop prices, exam marks, payroll.
 *
 * How to run:
 *   java Example04_BoundedTypes.java
 *
 * Output:
 *   Sum of Integer quantities: 60.0
 *   Sum of Double prices: 120.75
 *   Max mark: 92   Max name: Ravi   Max date-like string: 2025-03-10
 *   Stats -> count=4 avg=82.5 min=70.0 max=95.0
 *   Highest paid (sorted by pay): Meena earns 91000.0
 *   Total payroll: 218000.0
 */
import java.util.List;

public class Example04_BoundedTypes {

    // Without a bound we could not call doubleValue(): T might be String!
    static <T extends Number> double sum(List<T> numbers) {
        double total = 0;
        for (T n : numbers) {
            total += n.doubleValue();       // allowed: every Number has doubleValue()
        }
        return total;
    }

    // T must be comparable with itself, so a.compareTo(b) is allowed.
    static <T extends Comparable<T>> T max(List<T> items) {
        T best = items.get(0);
        for (T item : items) {
            if (item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }

    // MULTIPLE bounds: T must be a Payable AND Comparable to itself.
    static <T extends Payable & Comparable<T>> T highestPaid(List<T> people) {
        return max(people);                 // we can reuse max() because T is Comparable<T>
    }

    static <T extends Payable> double totalPay(List<T> people) {
        double total = 0;
        for (T p : people) {
            total += p.monthlyPay();        // allowed because T is a Payable
        }
        return total;
    }

    public static void main(String[] args) {
        System.out.println("Sum of Integer quantities: " + sum(List.of(10, 20, 30)));
        System.out.println("Sum of Double prices: " + sum(List.of(20.5, 100.25)));
        // sum(List.of("a", "b"));  // compile error: String is not a subclass of Number

        Integer topMark = max(List.of(78, 92, 85));
        String lastName = max(List.of("Asha", "Ravi", "Meena"));
        String latest = max(List.of("2025-01-05", "2025-03-10", "2024-12-31"));
        System.out.println("Max mark: " + topMark + "   Max name: " + lastName + "   Max date-like string: " + latest);

        Statistics<Integer> examStats = new Statistics<>(List.of(70, 95, 85, 80));
        System.out.println("Stats -> count=" + examStats.count() + " avg=" + examStats.average()
                + " min=" + examStats.min() + " max=" + examStats.max());
        // new Statistics<String>(...)  // compile error: String is not within the bound "T extends Number"

        List<Employee> staff = List.of(
                new Employee("Asha", 52000),
                new Employee("Meena", 91000),
                new Employee("Ravi", 75000));
        Employee top = highestPaid(staff);
        System.out.println("Highest paid (sorted by pay): " + top.getName() + " earns " + top.monthlyPay());
        System.out.println("Total payroll: " + totalPay(staff));
    }
}

// A bounded generic CLASS: only number types allowed.
class Statistics<T extends Number> {
    private final List<T> values;

    Statistics(List<T> values) {
        this.values = values;
    }

    int count() {
        return values.size();
    }

    double average() {
        double sum = 0;
        for (T v : values) sum += v.doubleValue();
        return sum / values.size();
    }

    double min() {
        double m = Double.MAX_VALUE;
        for (T v : values) m = Math.min(m, v.doubleValue());
        return m;
    }

    double max() {
        double m = -Double.MAX_VALUE;
        for (T v : values) m = Math.max(m, v.doubleValue());
        return m;
    }
}

interface Payable {
    double monthlyPay();
}

class Employee implements Payable, Comparable<Employee> {
    private final String name;
    private final double salary;

    Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    String getName() { return name; }

    @Override
    public double monthlyPay() { return salary; }

    @Override
    public int compareTo(Employee other) {
        return Double.compare(this.salary, other.salary);
    }
}
