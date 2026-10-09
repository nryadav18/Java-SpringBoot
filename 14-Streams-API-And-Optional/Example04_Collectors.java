/*
 * Example04_Collectors.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   The powerful Collectors - the "SQL GROUP BY" of Java:
 *   groupingBy (+ counting, averagingDouble, summingDouble, mapping),
 *   partitioningBy, joining, counting, averagingDouble,
 *   summarizingDouble, toMap with merge function.
 *
 *   We use TreeMap so maps print in sorted (predictable) order.
 *
 * HOW TO RUN:
 *   java Example04_Collectors.java
 */
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Example04_Collectors {

    static class Employee {
        private final String name;
        private final String department;
        private final String city;
        private final double salary;

        Employee(String name, String department, String city, double salary) {
            this.name = name;
            this.department = department;
            this.city = city;
            this.salary = salary;
        }

        String getName() { return name; }
        String getDepartment() { return department; }
        String getCity() { return city; }
        double getSalary() { return salary; }

        @Override
        public String toString() { return name; }
    }

    public static void main(String[] args) {
        List<Employee> employees = List.of(
                new Employee("Asha", "IT", "Chennai", 85000),
                new Employee("Ravi", "HR", "Pune", 45000),
                new Employee("John", "IT", "Pune", 95000),
                new Employee("Meena", "Finance", "Chennai", 60000),
                new Employee("Arjun", "IT", "Chennai", 70000),
                new Employee("Bala", "HR", "Delhi", 40000));

        // 1. groupingBy(classifier): Map<department, List<Employee>>
        Map<String, List<Employee>> byDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new, Collectors.toList()));
        System.out.println("1. By department      : " + byDept);

        // 2. groupingBy + counting(): how many per department
        Map<String, Long> countByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new, Collectors.counting()));
        System.out.println("2. Count per dept     : " + countByDept);

        // 3. groupingBy + averagingDouble(): average salary per department
        Map<String, Double> avgByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new,
                        Collectors.averagingDouble(Employee::getSalary)));
        System.out.println("3. Avg salary per dept: " + avgByDept);

        // 4. groupingBy + summingDouble(): total payroll per city
        Map<String, Double> payrollByCity = employees.stream()
                .collect(Collectors.groupingBy(Employee::getCity, TreeMap::new,
                        Collectors.summingDouble(Employee::getSalary)));
        System.out.println("4. Payroll per city   : " + payrollByCity);

        // 5. groupingBy + mapping(): only the NAMES per city
        Map<String, List<String>> namesByCity = employees.stream()
                .collect(Collectors.groupingBy(Employee::getCity, TreeMap::new,
                        Collectors.mapping(Employee::getName, Collectors.toList())));
        System.out.println("5. Names per city     : " + namesByCity);

        // 6. partitioningBy(Predicate): ALWAYS two groups -> true and false
        Map<Boolean, List<Employee>> highEarners = employees.stream()
                .collect(Collectors.partitioningBy(e -> e.getSalary() >= 60000));
        System.out.println("6. Salary >= 60k      : " + highEarners);

        // 7. joining(delimiter, prefix, suffix): build one String
        String nameList = employees.stream()
                .map(Employee::getName)
                .collect(Collectors.joining(", ", "[Team: ", "]"));
        System.out.println("7. Joined             : " + nameList);

        // 8. counting() and averagingDouble() on the whole stream
        long total = employees.stream().collect(Collectors.counting());
        double avg = employees.stream().collect(Collectors.averagingDouble(Employee::getSalary));
        System.out.println("8. Count / average    : " + total + " / " + avg);

        // 9. summarizingDouble(): count, sum, min, average, max in ONE pass
        DoubleSummaryStatistics stats = employees.stream()
                .collect(Collectors.summarizingDouble(Employee::getSalary));
        System.out.println("9. Stats              : min=" + stats.getMin() + " max=" + stats.getMax()
                + " avg=" + stats.getAverage() + " sum=" + stats.getSum());

        // 10. toMap with a MERGE function: department -> highest salary
        //     Without (a, b) -> Math.max(a, b), duplicate keys would throw IllegalStateException!
        Map<String, Double> maxSalaryByDept = employees.stream()
                .collect(Collectors.toMap(Employee::getDepartment, Employee::getSalary,
                        (a, b) -> Math.max(a, b), TreeMap::new));
        System.out.println("10. Max salary/dept   : " + maxSalaryByDept);

        // 11. Nested grouping: department -> city -> count
        Map<String, Map<String, Long>> deptCity = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new,
                        Collectors.groupingBy(Employee::getCity, TreeMap::new, Collectors.counting())));
        System.out.println("11. Dept -> city count: " + deptCity);
    }
}

/* Output:
1. By department      : {Finance=[Meena], HR=[Ravi, Bala], IT=[Asha, John, Arjun]}
2. Count per dept     : {Finance=1, HR=2, IT=3}
3. Avg salary per dept: {Finance=60000.0, HR=42500.0, IT=83333.33333333333}
4. Payroll per city   : {Chennai=215000.0, Delhi=40000.0, Pune=140000.0}
5. Names per city     : {Chennai=[Asha, Meena, Arjun], Delhi=[Bala], Pune=[Ravi, John]}
6. Salary >= 60k      : {false=[Ravi, Bala], true=[Asha, John, Meena, Arjun]}
7. Joined             : [Team: Asha, Ravi, John, Meena, Arjun, Bala]
8. Count / average    : 6 / 65833.33333333333
9. Stats              : min=40000.0 max=95000.0 avg=65833.33333333333 sum=395000.0
10. Max salary/dept   : {Finance=60000.0, HR=45000.0, IT=95000.0}
11. Dept -> city count: {Finance={Chennai=1}, HR={Delhi=1, Pune=1}, IT={Chennai=2, Pune=1}}
*/
