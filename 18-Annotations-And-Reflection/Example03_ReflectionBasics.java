import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;

/*
 * Example03_ReflectionBasics.java
 * ------------------------------------------------------------
 * What it shows:
 *   REFLECTION = code that looks at (and uses) other code while the program runs.
 *     - Getting a Class object (3 ways)
 *     - Listing fields, methods, constructors
 *     - Creating an object without writing 'new Student(...)' in the code
 *     - Calling a method by its NAME (a String)
 *     - Reading / changing a PRIVATE field with setAccessible(true)
 *
 *   Frameworks like Spring, Hibernate, Jackson and JUnit use exactly these tools.
 *
 * How to run:
 *   java Example03_ReflectionBasics.java
 */
public class Example03_ReflectionBasics {

    // A normal class we will "inspect"
    static class Student {
        private String name;
        private int marks;
        public static final String COLLEGE = "City College";

        public Student() {
            this("Unknown", 0);
        }

        public Student(String name, int marks) {
            this.name = name;
            this.marks = marks;
        }

        public String getName() {
            return name;
        }

        public String grade() {
            return marks >= 90 ? "A" : marks >= 75 ? "B" : "C";
        }

        public void addBonus(int bonus) {
            marks += bonus;
        }

        private String secret() {
            return "I like pizza";
        }

        @Override
        public String toString() {
            return "Student{name='" + name + "', marks=" + marks + "}";
        }
    }

    public static void main(String[] args) throws Exception {

        // ---------- 1) Three ways to get a Class object ----------
        Class<?> c1 = Student.class;                       // from the type name
        Class<?> c2 = new Student().getClass();            // from an object
        Class<?> c3 = Class.forName("Example03_ReflectionBasics$Student"); // from a String (nested class uses $)
        System.out.println("Same Class object? " + (c1 == c2 && c2 == c3));
        System.out.println("Simple name: " + c1.getSimpleName() + ", full name: " + c1.getName());

        // ---------- 2) Inspect fields ----------
        System.out.println("\nFields:");
        Field[] fields = c1.getDeclaredFields();           // ALL fields declared in this class (incl. private)
        Arrays.sort(fields, Comparator.comparing(Field::getName));
        for (Field f : fields) {
            System.out.println("  " + Modifier.toString(f.getModifiers()) + " "
                    + f.getType().getSimpleName() + " " + f.getName());
        }

        // ---------- 3) Inspect methods ----------
        System.out.println("\nMethods:");
        Method[] methods = c1.getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));
        for (Method m : methods) {
            System.out.println("  " + Modifier.toString(m.getModifiers()) + " "
                    + m.getReturnType().getSimpleName() + " " + m.getName()
                    + "(" + m.getParameterCount() + " params)");
        }

        // ---------- 4) Create an object via a constructor ----------
        Constructor<?> ctor = c1.getDeclaredConstructor(String.class, int.class);
        Object student = ctor.newInstance("Asha", 88);     // same as: new Student("Asha", 88)
        System.out.println("\nCreated with reflection: " + student);

        // No-arg constructor (this is how Spring/Hibernate/Jackson often create objects)
        Object empty = c1.getDeclaredConstructor().newInstance();
        System.out.println("No-arg constructor: " + empty);

        // ---------- 5) Call methods by name ----------
        Method grade = c1.getMethod("grade");              // public method, no parameters
        System.out.println("grade() returned: " + grade.invoke(student));

        Method addBonus = c1.getMethod("addBonus", int.class);
        addBonus.invoke(student, 5);                        // student.addBonus(5)
        System.out.println("After addBonus(5): " + student + ", grade now " + grade.invoke(student));

        // ---------- 6) Access PRIVATE members ----------
        Field nameField = c1.getDeclaredField("name");
        nameField.setAccessible(true);                     // switch off the 'private' check
        System.out.println("\nPrivate field name = " + nameField.get(student));
        nameField.set(student, "Asha Rao");                // change a private field!
        System.out.println("After changing it: " + student);

        Method secret = c1.getDeclaredMethod("secret");
        secret.setAccessible(true);
        System.out.println("Private method says: " + secret.invoke(student));

        // ---------- 7) Static field ----------
        Field college = c1.getField("COLLEGE");
        System.out.println("Static field COLLEGE = " + college.get(null)); // null: no object needed for static
    }
}

/*
 * Output:
 * Same Class object? true
 * Simple name: Student, full name: Example03_ReflectionBasics$Student
 *
 * Fields:
 *   public static final String COLLEGE
 *   private int marks
 *   private String name
 *
 * Methods:
 *   public void addBonus(1 params)
 *   public String getName(0 params)
 *   public String grade(0 params)
 *   private String secret(0 params)
 *   public String toString(0 params)
 *
 * Created with reflection: Student{name='Asha', marks=88}
 * No-arg constructor: Student{name='Unknown', marks=0}
 * grade() returned: B
 * After addBonus(5): Student{name='Asha', marks=93}, grade now A
 *
 * Private field name = Asha
 * After changing it: Student{name='Asha Rao', marks=93}
 * Private method says: I like pizza
 * Static field COLLEGE = City College
 */
