/*
 * Example02_Constructors.java
 * ------------------------------------------------------------
 * What this file shows (domain: a College Student):
 *   1. A CONSTRUCTOR is a special method that runs automatically when you write 'new'.
 *      It has the SAME name as the class and NO return type.
 *   2. Default constructor (no parameters).
 *   3. Parameterized constructor (values passed in).
 *   4. Constructor OVERLOADING (several constructors with different parameters).
 *   5. The 'this' keyword: "this object's field" (fixes name clashes).
 *   6. Constructor CHAINING with this(...): one constructor calls another to avoid repeating code.
 *
 * How to run (from this folder):
 *   java Example02_Constructors.java
 */
public class Example02_Constructors {

    public static void main(String[] args) {
        System.out.println("--- Creating s1 with no arguments ---");
        Student s1 = new Student();

        System.out.println("--- Creating s2 with name only ---");
        Student s2 = new Student("Arjun");

        System.out.println("--- Creating s3 with all details ---");
        Student s3 = new Student("Meera", 21, "Computer Science");

        System.out.println();
        s1.printDetails();
        s2.printDetails();
        s3.printDetails();

        // A class WITHOUT any constructor still works: Java adds an invisible default one.
        Course course = new Course();
        System.out.println("Course title (never set): " + course.title);
    }
}

class Student {
    String name;
    int age;
    String department;

    // 1) Default (no-argument) constructor -> sensible starting values.
    //    It CHAINS to the 3-argument constructor using this(...).
    Student() {
        this("Unknown", 18, "Not assigned");   // must be the FIRST line
        System.out.println("  [no-arg constructor finished]");
    }

    // 2) Overloaded constructor: only a name. Also chains.
    Student(String name) {
        this(name, 18, "Not assigned");
        System.out.println("  [name-only constructor finished]");
    }

    // 3) The "main" constructor. All the real work happens in ONE place.
    //    The parameter 'name' hides the field 'name', so we write this.name for the field.
    Student(String name, int age, String department) {
        System.out.println("  [3-arg constructor running for " + name + "]");
        this.name = name;              // field = parameter
        this.age = age;
        this.department = department;
    }

    void printDetails() {
        // Inside a normal method, 'name' means this.name (no clash here), but
        // writing 'this.' is allowed too.
        System.out.println(name + " | age " + this.age + " | " + department);
    }
}

/** No constructor written -> the compiler adds: Course() { } */
class Course {
    String title;
}

/*
Output:
--- Creating s1 with no arguments ---
  [3-arg constructor running for Unknown]
  [no-arg constructor finished]
--- Creating s2 with name only ---
  [3-arg constructor running for Arjun]
  [name-only constructor finished]
--- Creating s3 with all details ---
  [3-arg constructor running for Meera]

Unknown | age 18 | Not assigned
Arjun | age 18 | Not assigned
Meera | age 21 | Computer Science
Course title (never set): null
*/
