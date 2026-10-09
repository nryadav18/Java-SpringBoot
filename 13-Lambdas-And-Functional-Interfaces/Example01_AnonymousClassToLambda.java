/*
 * Example01_AnonymousClassToLambda.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   How Java code evolved from a full class -> anonymous class -> lambda.
 *   All three versions do EXACTLY the same job: decide the order of
 *   books in a library list (shortest title first).
 *
 * HOW TO RUN (from this folder):
 *   java Example01_AnonymousClassToLambda.java
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Example01_AnonymousClassToLambda {

    // ------------------------------------------------------------
    // STEP 1: The "old school" way - write a whole named class
    // just to hold ONE method (compare). Lots of typing!
    // ------------------------------------------------------------
    static class ByTitleLength implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return a.length() - b.length();   // shorter title comes first
        }
    }

    public static void main(String[] args) {
        List<String> books = new ArrayList<>(Arrays.asList(
                "Clean Code", "Java", "Spring in Action", "Head First"));

        // ---- Version 1: separate named class ----
        books.sort(new ByTitleLength());
        System.out.println("1) Named class      : " + books);

        // ---- Version 2: anonymous class (a class with no name, written inline) ----
        // Shorter: no separate class, but still lots of "ceremony"
        // (new Comparator<String>() { @Override public int compare(...) {...} })
        books = new ArrayList<>(Arrays.asList(
                "Clean Code", "Java", "Spring in Action", "Head First"));
        books.sort(new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.length() - b.length();
            }
        });
        System.out.println("2) Anonymous class  : " + books);

        // ---- Version 3: lambda (Java 8+) ----
        // We only write the part that MATTERS: inputs -> result.
        // Java already knows sort() needs a Comparator<String>,
        // so it figures out the types of a and b by itself.
        books = new ArrayList<>(Arrays.asList(
                "Clean Code", "Java", "Spring in Action", "Head First"));
        books.sort((a, b) -> a.length() - b.length());
        System.out.println("3) Lambda           : " + books);

        // ---- Bonus: a Runnable as anonymous class vs lambda ----
        Runnable oldWay = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello from an anonymous Runnable");
            }
        };
        Runnable newWay = () -> System.out.println("Hello from a lambda Runnable");

        oldWay.run();   // nothing happens until we CALL run()
        newWay.run();
    }
}

/* Output:
1) Named class      : [Java, Clean Code, Head First, Spring in Action]
2) Anonymous class  : [Java, Clean Code, Head First, Spring in Action]
3) Lambda           : [Java, Clean Code, Head First, Spring in Action]
Hello from an anonymous Runnable
Hello from a lambda Runnable
*/
