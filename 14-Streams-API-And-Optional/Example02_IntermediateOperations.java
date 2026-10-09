/*
 * Example02_IntermediateOperations.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   Intermediate operations - they return a NEW stream, so you can chain them:
 *   filter, map, flatMap, sorted, distinct, limit, skip, peek, mapToDouble
 *
 *   Domain: employees of a small IT company.
 *
 * HOW TO RUN:
 *   java Example02_IntermediateOperations.java
 */
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Example02_IntermediateOperations {

    static class Employee {
        private final String name;
        private final String department;
        private final double salary;
        private final List<String> skills;

        Employee(String name, String department, double salary, List<String> skills) {
            this.name = name;
            this.department = department;
            this.salary = salary;
            this.skills = skills;
        }

        String getName() { return name; }
        String getDepartment() { return department; }
        double getSalary() { return salary; }
        List<String> getSkills() { return skills; }

        @Override
        public String toString() { return name; }
    }

    public static void main(String[] args) {
        List<Employee> employees = List.of(
                new Employee("Asha", "IT", 85000, List.of("Java", "Spring")),
                new Employee("Ravi", "HR", 45000, List.of("Excel")),
                new Employee("John", "IT", 95000, List.of("Java", "Docker", "AWS")),
                new Employee("Meena", "Finance", 60000, List.of("Excel", "SQL")),
                new Employee("Arjun", "IT", 70000, List.of("Spring", "SQL")),
                new Employee("Bala", "HR", 40000, List.of("Communication")));

        // filter(Predicate): keep only matching elements
        System.out.println("IT employees      : " + employees.stream()
                .filter(e -> e.getDepartment().equals("IT"))
                .collect(Collectors.toList()));

        // map(Function): transform each element into something else
        System.out.println("Names             : " + employees.stream()
                .map(Employee::getName)
                .collect(Collectors.toList()));

        System.out.println("Name lengths      : " + employees.stream()
                .map(e -> e.getName().length())
                .collect(Collectors.toList()));

        // map gives a stream of LISTS (Stream<List<String>>) - not what we want...
        System.out.println("map(skills)       : " + employees.stream()
                .map(Employee::getSkills)
                .collect(Collectors.toList()));

        // flatMap(Function -> Stream): flatten "list of lists" into one stream
        System.out.println("flatMap(skills)   : " + employees.stream()
                .flatMap(e -> e.getSkills().stream())
                .collect(Collectors.toList()));

        // distinct(): remove duplicates (uses equals/hashCode)
        System.out.println("Unique skills     : " + employees.stream()
                .flatMap(e -> e.getSkills().stream())
                .distinct()
                .collect(Collectors.toList()));

        // sorted(): natural order (A..Z for Strings)
        System.out.println("Skills A-Z        : " + employees.stream()
                .flatMap(e -> e.getSkills().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList()));

        // sorted(Comparator): custom order - highest salary first
        System.out.println("By salary desc    : " + employees.stream()
                .sorted(Comparator.comparing(Employee::getSalary).reversed())
                .collect(Collectors.toList()));

        // limit(n): take only the first n -> "Top 3 earners"
        System.out.println("Top 3 earners     : " + employees.stream()
                .sorted(Comparator.comparing(Employee::getSalary).reversed())
                .limit(3)
                .map(Employee::getName)
                .collect(Collectors.toList()));

        // skip(n) + limit(n) = PAGINATION (page 2, page size 2)
        int page = 2, size = 2;
        System.out.println("Page 2 (size 2)   : " + employees.stream()
                .skip((long) (page - 1) * size)
                .limit(size)
                .collect(Collectors.toList()));

        // mapToDouble: switch to a DoubleStream to use sum()/average()
        double itPayroll = employees.stream()
                .filter(e -> e.getDepartment().equals("IT"))
                .mapToDouble(Employee::getSalary)
                .sum();
        System.out.println("IT payroll        : " + itPayroll);

        // Chaining many steps: names of IT people earning > 75k, upper case, sorted
        System.out.println("Chained           : " + employees.stream()
                .filter(e -> e.getDepartment().equals("IT"))
                .filter(e -> e.getSalary() > 75000)
                .map(Employee::getName)
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList()));
    }
}

/* Output:
IT employees      : [Asha, John, Arjun]
Names             : [Asha, Ravi, John, Meena, Arjun, Bala]
Name lengths      : [4, 4, 4, 5, 5, 4]
map(skills)       : [[Java, Spring], [Excel], [Java, Docker, AWS], [Excel, SQL], [Spring, SQL], [Communication]]
flatMap(skills)   : [Java, Spring, Excel, Java, Docker, AWS, Excel, SQL, Spring, SQL, Communication]
Unique skills     : [Java, Spring, Excel, Docker, AWS, SQL, Communication]
Skills A-Z        : [AWS, Communication, Docker, Excel, Java, SQL, Spring]
By salary desc    : [John, Asha, Arjun, Meena, Ravi, Bala]
Top 3 earners     : [John, Asha, Arjun]
Page 2 (size 2)   : [John, Meena]
IT payroll        : 250000.0
Chained           : [ASHA, JOHN]
*/
