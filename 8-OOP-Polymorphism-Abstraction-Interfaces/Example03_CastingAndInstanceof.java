/*
 * Example03_CastingAndInstanceof.java
 * ------------------------------------------------------------
 * What this file shows (domain: a Company):
 *   1. UPCASTING   (child -> parent type): automatic and always safe.
 *   2. Through a parent-type variable you can only call PARENT methods.
 *   3. DOWNCASTING (parent -> child type): needs an explicit cast (Manager) and can FAIL
 *      at runtime with ClassCastException if the object is not really that type.
 *   4. instanceof: check the real type BEFORE downcasting.
 *   5. Pattern matching for instanceof (Java 16+): check + cast + new variable in one step.
 *
 * How to run (from this folder):
 *   java Example03_CastingAndInstanceof.java
 */
public class Example03_CastingAndInstanceof {

    public static void main(String[] args) {

        // ---------- 1. Upcasting ----------
        Manager manager = new Manager("Anil", 8);
        Employee employee = manager;          // upcast: automatic, no (Employee) needed
        employee.work();                      // runs Manager's overridden work()

        // employee.approveLeave();           // COMPILE ERROR: the variable type is Employee,
        //                                    // and Employee has no approveLeave() method.

        // ---------- 3. Downcasting ----------
        Manager sameManager = (Manager) employee;   // explicit cast: "trust me, it's a Manager"
        sameManager.approveLeave();                 // now Manager-only methods are available

        Employee intern = new Intern("Zoya", "Spring Boot");
        try {
            Manager wrong = (Manager) intern;       // compiles ... but the object is an Intern!
            wrong.approveLeave();
        } catch (ClassCastException e) {
            System.out.println("ClassCastException: an Intern is not a Manager!");
        }

        // ---------- 4. instanceof ----------
        System.out.println();
        System.out.println("intern instanceof Employee: " + (intern instanceof Employee)); // true
        System.out.println("intern instanceof Intern  : " + (intern instanceof Intern));   // true
        System.out.println("intern instanceof Manager : " + (intern instanceof Manager));  // false
        Employee nobody = null;
        System.out.println("null instanceof Employee  : " + (nobody instanceof Employee)); // false (never crashes)

        // ---------- 5. Old style vs pattern matching ----------
        System.out.println();
        Employee[] team = { manager, intern, new Employee("Kavya") };
        for (Employee e : team) {
            describeOldStyle(e);
            describeWithPattern(e);
        }
    }

    // Before Java 16: check, then cast on a separate line.
    static void describeOldStyle(Employee e) {
        if (e instanceof Manager) {
            Manager m = (Manager) e;          // repeat the type, extra line
            System.out.println("[old] " + e.getName() + " manages " + m.getTeamSize() + " people");
        } else {
            System.out.println("[old] " + e.getName() + " is not a manager");
        }
    }

    // Java 16+: 'e instanceof Intern i' checks AND creates the variable i (already cast).
    static void describeWithPattern(Employee e) {
        if (e instanceof Manager m) {
            System.out.println("[new] " + m.getName() + " manages " + m.getTeamSize() + " people");
        } else if (e instanceof Intern i && i.getTopic().contains("Spring")) {   // can combine with &&
            System.out.println("[new] " + i.getName() + " is an intern learning " + i.getTopic());
        } else {
            System.out.println("[new] " + e.getName() + " is a regular employee");
        }
    }
}

class Employee {
    private final String name;

    Employee(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    void work() {
        System.out.println(name + " is working");
    }
}

class Manager extends Employee {
    private final int teamSize;

    Manager(String name, int teamSize) {
        super(name);
        this.teamSize = teamSize;
    }

    int getTeamSize() {
        return teamSize;
    }

    @Override
    void work() {
        System.out.println(getName() + " is planning work for the team");
    }

    void approveLeave() {
        System.out.println(getName() + " approved a leave request");
    }
}

class Intern extends Employee {
    private final String topic;

    Intern(String name, String topic) {
        super(name);
        this.topic = topic;
    }

    String getTopic() {
        return topic;
    }
}

/*
Output:
Anil is planning work for the team
Anil approved a leave request
ClassCastException: an Intern is not a Manager!

intern instanceof Employee: true
intern instanceof Intern  : true
intern instanceof Manager : false
null instanceof Employee  : false

[old] Anil manages 8 people
[new] Anil manages 8 people
[old] Zoya is not a manager
[new] Zoya is an intern learning Spring Boot
[old] Kavya is not a manager
[new] Kavya is a regular employee
*/
