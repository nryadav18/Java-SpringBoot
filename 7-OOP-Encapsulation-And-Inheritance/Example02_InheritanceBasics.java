/*
 * Example02_InheritanceBasics.java
 * ------------------------------------------------------------
 * What this file shows (domain: a Company):
 *   1. INHERITANCE with 'extends': Manager and Developer ARE Employees.
 *   2. The child class automatically gets the parent's (non-private) methods.
 *   3. The child can ADD its own fields and methods.
 *   4. super(...) passes values up to the parent's constructor.
 *   5. Code written ONCE in Employee is re-used by every child.
 *
 * How to run (from this folder):
 *   java Example02_InheritanceBasics.java
 */
public class Example02_InheritanceBasics {

    public static void main(String[] args) {
        Manager manager = new Manager("Anil", 120_000, 8);
        Developer developer = new Developer("Divya", 90_000, "Java");

        // Methods INHERITED from Employee - we never wrote them in Manager/Developer:
        manager.printBasicInfo();
        developer.printBasicInfo();
        developer.giveRaise(10);                       // inherited behaviour
        System.out.println(developer.getName() + "'s new salary: " + developer.getSalary());

        // Methods that exist ONLY in the child classes:
        manager.conductMeeting();
        developer.writeCode();

        // IS-A check: a Manager IS-A Employee
        System.out.println("manager instanceof Employee? " + (manager instanceof Employee));
    }
}

/** PARENT class (also called superclass or base class). Common things for ALL employees. */
class Employee {
    private String name;      // private: even children can't touch it directly ...
    private double salary;    // ... they use the public getters/methods instead.

    Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public double getSalary() {
        return salary;
    }

    public void giveRaise(double percent) {
        salary = salary + salary * percent / 100;
    }

    public void printBasicInfo() {
        System.out.println(getClass().getSimpleName() + ": " + name + ", salary " + salary);
    }
}

/** CHILD class (subclass / derived class). Manager IS-A Employee + extra things. */
class Manager extends Employee {
    private int teamSize;     // extra field only managers have

    Manager(String name, double salary, int teamSize) {
        super(name, salary);  // FIRST: let Employee set up name and salary
        this.teamSize = teamSize;
    }

    public void conductMeeting() {
        System.out.println(getName() + " is conducting a meeting with " + teamSize + " people");
    }
}

/** Another CHILD class of the same parent. */
class Developer extends Employee {
    private String primaryLanguage;

    Developer(String name, double salary, String primaryLanguage) {
        super(name, salary);
        this.primaryLanguage = primaryLanguage;
    }

    public void writeCode() {
        System.out.println(getName() + " is writing " + primaryLanguage + " code");
    }
}

/*
Output:
Manager: Anil, salary 120000.0
Developer: Divya, salary 90000.0
Divya's new salary: 99000.0
Anil is conducting a meeting with 8 people
Divya is writing Java code
manager instanceof Employee? true
*/
