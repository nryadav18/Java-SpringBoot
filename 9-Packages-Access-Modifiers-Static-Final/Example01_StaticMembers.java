/*
 * Example01_StaticMembers.java
 *
 * What this file demonstrates:
 *   - static field   : ONE copy shared by ALL objects (a class-level variable)
 *   - instance field : EACH object gets its OWN copy
 *   - static method  : called on the class (Student.getTotalStudents()), no object needed
 *   - static block   : runs ONCE, when the class is first loaded by the JVM
 *   - instance block : runs every time an object is created (rarely used, shown for completeness)
 *   - why a static method cannot use instance fields directly
 *
 * Domain: a college counting how many students have joined.
 *
 * How to run:
 *   java Example01_StaticMembers.java
 *
 * Output:
 *   [static block] Student class loaded. College = City College
 *   Students before anyone joins: 0
 *   [instance block] creating a Student object...
 *   [instance block] creating a Student object...
 *   [instance block] creating a Student object...
 *   Asha has roll number 1
 *   Ravi has roll number 2
 *   Meena has roll number 3
 *   Total students: 3
 *   Same value via an object (works, but NOT recommended): 3
 *   College renamed for everyone -> Asha: Sunrise College, Ravi: Sunrise College
 *   Max of 7 and 12 using Math.max (a static method): 12
 *   isValidAge(15)? false   isValidAge(19)? true
 */
public class Example01_StaticMembers {

    public static void main(String[] args) {
        // We can use static members BEFORE creating any object:
        System.out.println("Students before anyone joins: " + Student.getTotalStudents());

        Student asha = new Student("Asha");
        Student ravi = new Student("Ravi");
        Student meena = new Student("Meena");

        asha.printInfo();
        ravi.printInfo();
        meena.printInfo();

        // Recommended: call a static method using the CLASS name.
        System.out.println("Total students: " + Student.getTotalStudents());

        // Allowed but confusing: calling a static method through an object.
        // It still reads the ONE shared counter. VS Code shows a warning for this.
        System.out.println("Same value via an object (works, but NOT recommended): " + asha.getTotalStudents());

        // Changing a static field changes it for EVERY object (because there is only one copy).
        Student.collegeName = "Sunrise College";
        System.out.println("College renamed for everyone -> Asha: " + asha.getCollege()
                + ", Ravi: " + ravi.getCollege());

        // You have already used static methods from the JDK:
        System.out.println("Max of 7 and 12 using Math.max (a static method): " + Math.max(7, 12));

        // A static "helper" method: needs no object at all.
        System.out.println("isValidAge(15)? " + Student.isValidAge(15)
                + "   isValidAge(19)? " + Student.isValidAge(19));
    }
}

class Student {

    // ---------- static (class-level) ----------
    static String collegeName;          // shared by all students
    private static int totalStudents;   // shared counter, starts at 0

    // static block: runs ONE time when the JVM loads the Student class
    // (that is, the first time the class is used). Good for one-time setup.
    static {
        collegeName = "City College";
        System.out.println("[static block] Student class loaded. College = " + collegeName);
    }

    // ---------- instance (object-level) ----------
    private final String name;
    private final int rollNumber;

    // instance initializer block: runs before the constructor body, for EVERY new object.
    {
        System.out.println("[instance block] creating a Student object...");
    }

    Student(String name) {
        this.name = name;
        totalStudents++;                  // update the shared counter
        this.rollNumber = totalStudents;  // each student gets the next number
    }

    // static method: belongs to the class.
    static int getTotalStudents() {
        // System.out.println(name);  <- would NOT compile:
        // "non-static variable name cannot be referenced from a static context".
        // Reason: a static method has no "this" object, so WHICH student's name would it print?
        return totalStudents;
    }

    static boolean isValidAge(int age) {
        return age >= 17;   // pretend the college admits students aged 17+
    }

    // instance method: can use BOTH instance fields and static fields.
    void printInfo() {
        System.out.println(name + " has roll number " + rollNumber);
    }

    String getCollege() {
        return collegeName;
    }
}
