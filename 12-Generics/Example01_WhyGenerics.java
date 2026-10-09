/*
 * Example01_WhyGenerics.java
 *
 * What this file demonstrates:
 *   - Life BEFORE generics (Java 1.4 and earlier): a "raw" List holds Object,
 *       -> you must CAST every time you take something out,
 *       -> a wrong element is only discovered at RUNTIME (ClassCastException).
 *   - Life WITH generics: List<String>
 *       -> no casts,
 *       -> the COMPILER rejects wrong elements immediately (type safety).
 *   - A home-made "ObjectBox" vs a generic "Box<T>" preview.
 *
 * Domain: a list of customer email addresses.
 *
 * How to run:
 *   java Example01_WhyGenerics.java
 *   (Remove the @SuppressWarnings line and compile with "javac -Xlint:unchecked" to see the
 *    compiler WARN you about the raw list - exactly the point of this lesson.)
 *
 * Output:
 *   --- Without generics (raw List) ---
 *   asha@mail.com -> ASHA@MAIL.COM
 *   ravi@mail.com -> RAVI@MAIL.COM
 *   Crash at runtime: ClassCastException (class java.lang.Integer cannot be cast to class java.lang.String ...)
 *   --- With generics (List<String>) ---
 *   asha@mail.com -> ASHA@MAIL.COM
 *   ravi@mail.com -> RAVI@MAIL.COM
 *   --- ObjectBox vs Box<T> ---
 *   Needed a cast: Laptop
 *   No cast needed: Laptop
 */
import java.util.ArrayList;
import java.util.List;

public class Example01_WhyGenerics {

    @SuppressWarnings({"rawtypes", "unchecked"})   // we are using raw types ON PURPOSE to show the problem
    static void withoutGenerics() {
        System.out.println("--- Without generics (raw List) ---");
        List emails = new ArrayList();    // a RAW list: it holds "Object" - anything goes
        emails.add("asha@mail.com");
        emails.add("ravi@mail.com");
        emails.add(42);                   // oops - a number sneaks in. The compiler does NOT stop us.

        try {
            for (Object o : emails) {
                String email = (String) o;   // we must CAST... and the cast fails for 42
                System.out.println(email + " -> " + email.toUpperCase());
            }
        } catch (ClassCastException e) {
            String msg = e.getMessage();
            System.out.println("Crash at runtime: ClassCastException ("
                    + msg.substring(0, msg.indexOf("java.lang.String") + "java.lang.String".length()) + " ...)");
        }
    }

    static void withGenerics() {
        System.out.println("--- With generics (List<String>) ---");
        List<String> emails = new ArrayList<>();   // "a list of String"
        emails.add("asha@mail.com");
        emails.add("ravi@mail.com");
        // emails.add(42);   // COMPILE ERROR: incompatible types: int cannot be converted to String
        //                      The bug is caught BEFORE the program ever runs.

        for (String email : emails) {               // no cast needed
            System.out.println(email + " -> " + email.toUpperCase());
        }
    }

    public static void main(String[] args) {
        withoutGenerics();
        withGenerics();

        System.out.println("--- ObjectBox vs Box<T> ---");
        ObjectBox oldBox = new ObjectBox("Laptop");
        String a = (String) oldBox.get();            // cast needed, and nothing stops ObjectBox(42)
        System.out.println("Needed a cast: " + a);

        Box<String> newBox = new Box<>("Laptop");    // T is String for this box
        String b = newBox.get();                     // no cast: the compiler KNOWS it is a String
        System.out.println("No cast needed: " + b);
    }
}

// The "old" way: works for any type by using Object, but loses all type information.
class ObjectBox {
    private final Object item;
    ObjectBox(Object item) { this.item = item; }
    Object get() { return item; }
}

// The generic way: T is a "type parameter" - a placeholder filled in by whoever uses the class.
class Box<T> {
    private final T item;
    Box(T item) { this.item = item; }
    T get() { return item; }
}
