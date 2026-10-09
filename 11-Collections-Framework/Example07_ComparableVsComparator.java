/*
 * Example07_ComparableVsComparator.java
 *
 * What this file demonstrates:
 *   - Sorting Strings/Integers works out of the box (they are Comparable).
 *   - Comparable<T> : the object's ONE "natural" order, written INSIDE the class (compareTo).
 *   - Comparator<T> : any number of EXTRA orders, written OUTSIDE the class (compare).
 *   - compareTo/compare return: negative (this comes first), 0 (equal), positive (other comes first)
 *   - Writing a Comparator as (a) a separate class, (b) an anonymous class (Topic 9),
 *     (c) a short preview of the modern style: Comparator.comparing(...) (explained in Topic 13).
 *   - reversed() and thenComparing() for multi-level sorting.
 *   - TreeSet / TreeMap / PriorityQueue use the same ordering rules.
 *
 * Domain: employees.
 *
 * How to run:
 *   java Example07_ComparableVsComparator.java
 *
 * Output:
 *   Names sorted: [Asha, Dev, Meena, Ravi]
 *   Natural order (by id):
 *     [101 Ravi, IT, 75000.0, 102 Asha, HR, 52000.0, 103 Meena, IT, 91000.0, 104 Dev, HR, 52000.0]
 *   By name (Comparator class):
 *     [102 Asha, HR, 52000.0, 104 Dev, HR, 52000.0, 103 Meena, IT, 91000.0, 101 Ravi, IT, 75000.0]
 *   By salary, highest first (anonymous class):
 *     [103 Meena, IT, 91000.0, 101 Ravi, IT, 75000.0, 102 Asha, HR, 52000.0, 104 Dev, HR, 52000.0]
 *   By department, then salary desc, then name (Comparator.comparing):
 *     [102 Asha, HR, 52000.0, 104 Dev, HR, 52000.0, 103 Meena, IT, 91000.0, 101 Ravi, IT, 75000.0]
 *   TreeSet with a name comparator: [Asha, Dev, Meena, Ravi]
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class Example07_ComparableVsComparator {

    public static void main(String[] args) {
        List<String> names = new ArrayList<>(List.of("Ravi", "Asha", "Meena", "Dev"));
        Collections.sort(names);                       // String already knows how to compare itself
        System.out.println("Names sorted: " + names);

        List<Employee> staff = new ArrayList<>();
        staff.add(new Employee(103, "Meena", "IT", 91000));
        staff.add(new Employee(101, "Ravi", "IT", 75000));
        staff.add(new Employee(104, "Dev", "HR", 52000));
        staff.add(new Employee(102, "Asha", "HR", 52000));

        // 1. Comparable: uses Employee.compareTo -> natural order = by id
        Collections.sort(staff);
        System.out.println("Natural order (by id):");
        System.out.println("  " + staff);

        // 2a. Comparator as a separate, reusable class
        staff.sort(new NameComparator());              // list.sort(comparator) - same as Collections.sort(list, c)
        System.out.println("By name (Comparator class):");
        System.out.println("  " + staff);

        // 2b. Comparator as an anonymous class - used once, no name needed
        staff.sort(new Comparator<Employee>() {
            @Override
            public int compare(Employee a, Employee b) {
                return Double.compare(b.getSalary(), a.getSalary());   // b before a -> DESCENDING
            }
        });
        System.out.println("By salary, highest first (anonymous class):");
        System.out.println("  " + staff);

        // 2c. PREVIEW of the modern style (Topic 13 explains "Employee::getDepartment").
        //     Read it like English: "compare by department, then by salary reversed, then by name".
        Comparator<Employee> byDeptThenSalaryDescThenName =
                Comparator.comparing(Employee::getDepartment)
                          .thenComparing(Comparator.comparing(Employee::getSalary).reversed())
                          .thenComparing(Employee::getName);
        staff.sort(byDeptThenSalaryDescThenName);
        System.out.println("By department, then salary desc, then name (Comparator.comparing):");
        System.out.println("  " + staff);

        // Sorted collections accept a Comparator in their constructor
        TreeSet<Employee> byName = new TreeSet<>(new NameComparator());
        byName.addAll(staff);
        StringBuilder sb = new StringBuilder();
        for (Employee e : byName) {
            sb.append(sb.length() == 0 ? "" : ", ").append(e.getName());
        }
        System.out.println("TreeSet with a name comparator: [" + sb + "]");
    }
}

// Comparable<Employee> = "Employees know how to compare themselves with other Employees"
class Employee implements Comparable<Employee> {
    private final int id;
    private final String name;
    private final String department;
    private final double salary;

    Employee(int id, String name, String department, double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    int getId()              { return id; }
    String getName()         { return name; }
    String getDepartment()   { return department; }
    double getSalary()       { return salary; }

    // Natural order: by id, ascending.
    //  negative -> this comes BEFORE other
    //  zero     -> same position
    //  positive -> this comes AFTER other
    @Override
    public int compareTo(Employee other) {
        return Integer.compare(this.id, other.id);   // safer than "this.id - other.id" (no overflow)
    }

    @Override
    public String toString() {
        return id + " " + name + ", " + department + ", " + salary;
    }
}

// A separate Comparator class: an "extra" way of ordering, outside Employee.
class NameComparator implements Comparator<Employee> {
    @Override
    public int compare(Employee a, Employee b) {
        return a.getName().compareTo(b.getName());   // String's own compareTo = alphabetical
    }
}
