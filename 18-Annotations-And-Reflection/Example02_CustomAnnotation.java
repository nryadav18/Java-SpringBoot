import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/*
 * Example02_CustomAnnotation.java
 * ------------------------------------------------------------
 * What it shows:
 *   - How to WRITE your own annotation with @interface
 *   - Meta-annotations (annotations on annotations):
 *       @Retention(RUNTIME) : keep the annotation in the .class file AND at runtime,
 *                             so reflection can read it. (Default is CLASS = invisible at runtime!)
 *       @Target(...)        : WHERE it may be used (class, method, field, parameter...)
 *   - Annotation elements (like parameters), default values, the special 'value' element
 *   - How to READ annotations at runtime with reflection and act on them.
 *
 * Mini-project: a tiny "test runner" (like JUnit!) that finds methods marked @RunMe
 *               and calls them, and a @Role annotation for simple "security".
 *
 * How to run:
 *   java Example02_CustomAnnotation.java
 */
public class Example02_CustomAnnotation {

    // ---------- 1) Declaring custom annotations ----------

    // A "marker" annotation: no elements, just a label.
    @Retention(RetentionPolicy.RUNTIME)    // must be RUNTIME so we can read it while the program runs
    @Target(ElementType.METHOD)            // may only be placed on methods
    @interface RunMe {
    }

    // An annotation WITH elements. Elements look like methods without bodies.
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})   // methods and classes
    @interface Role {
        String value();                     // special name: can be written @Role("ADMIN")
        int priority() default 1;           // optional element with a default value
    }

    // ---------- 2) Using them on a class ----------
    @Role(value = "STAFF", priority = 5)
    static class ShopTasks {

        @RunMe
        public void checkStock() {
            System.out.println("  -> checking stock levels...");
        }

        @RunMe
        @Role("ADMIN")                      // short form, 'value' only
        public void deleteOldOrders() {
            System.out.println("  -> deleting old orders...");
        }

        public void notAnnotated() {
            System.out.println("  -> you should NOT see this line");
        }

        @RunMe
        @Role(value = "MANAGER", priority = 3)
        public void printDailyReport() {
            System.out.println("  -> printing daily report...");
        }
    }

    public static void main(String[] args) throws Exception {
        Class<ShopTasks> clazz = ShopTasks.class;       // the "Class object" = description of the class

        // Read an annotation on the CLASS
        Role classRole = clazz.getAnnotation(Role.class);
        System.out.println("Class " + clazz.getSimpleName() + " has @Role(value=" + classRole.value()
                + ", priority=" + classRole.priority() + ")");

        // Create one object to call methods on
        ShopTasks tasks = new ShopTasks();

        System.out.println("\nRunning every method annotated with @RunMe:");
        // getDeclaredMethods() order is NOT guaranteed, so we sort by name for a stable output
        Method[] methods = clazz.getDeclaredMethods();
        java.util.Arrays.sort(methods, java.util.Comparator.comparing(Method::getName));

        for (Method m : methods) {
            if (m.isAnnotationPresent(RunMe.class)) {
                Role role = m.getAnnotation(Role.class);    // null if not present
                String who = (role == null) ? "anyone" : role.value() + " (priority " + role.priority() + ")";
                System.out.println("* " + m.getName() + "  [allowed: " + who + "]");
                m.invoke(tasks);                            // call the method via reflection
            }
        }
    }
}

/*
 * Output:
 * Class ShopTasks has @Role(value=STAFF, priority=5)
 *
 * Running every method annotated with @RunMe:
 * * checkStock  [allowed: anyone]
 *   -> checking stock levels...
 * * deleteOldOrders  [allowed: ADMIN (priority 1)]
 *   -> deleting old orders...
 * * printDailyReport  [allowed: MANAGER (priority 3)]
 *   -> printing daily report...
 */
