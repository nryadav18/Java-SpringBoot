/*
 * Example03_BuiltInFunctionalInterfaces.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   The ready-made functional interfaces in java.util.function
 *   (plus Runnable and Comparator). You almost never need to write
 *   your own interface - Java already has one for most shapes:
 *
 *     Predicate<T>        T -> boolean      "test / check"
 *     Function<T,R>       T -> R            "convert / transform"
 *     Consumer<T>         T -> (nothing)    "use / print / save"
 *     Supplier<T>         () -> T           "give me / create"
 *     BiFunction<T,U,R>   (T,U) -> R        "two inputs, one result"
 *     UnaryOperator<T>    T -> T            "same type in and out"
 *     BinaryOperator<T>   (T,T) -> T        "combine two of same type"
 *
 * HOW TO RUN:
 *   java Example03_BuiltInFunctionalInterfaces.java
 */
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class Example03_BuiltInFunctionalInterfaces {

    public static void main(String[] args) {

        // ---------- Runnable: () -> nothing ----------
        Runnable openShop = () -> System.out.println("Shop is open!");
        openShop.run();

        // ---------- Predicate<T>: T -> boolean ----------
        // Method to call: test(...)
        Predicate<Integer> isAdult = age -> age >= 18;
        System.out.println("Is 20 adult? " + isAdult.test(20));
        System.out.println("Is 15 adult? " + isAdult.test(15));

        // ---------- Function<T, R>: T -> R ----------
        // Method to call: apply(...)
        Function<String, Integer> wordLength = word -> word.length();
        System.out.println("Length of 'Spring' = " + wordLength.apply("Spring"));

        // ---------- Consumer<T>: T -> nothing ----------
        // Method to call: accept(...)
        Consumer<String> sendSms = msg -> System.out.println("SMS sent: " + msg);
        sendSms.accept("Your order is on the way");

        // ---------- Supplier<T>: nothing -> T ----------
        // Method to call: get()
        Supplier<String> orderIdMaker = () -> "ORD-" + 1001;
        System.out.println("New order id: " + orderIdMaker.get());
        Random random = new Random(42); // fixed seed = same numbers every run
        Supplier<Integer> otp = () -> 1000 + random.nextInt(9000);
        System.out.println("OTP is 4 digits? " + (otp.get().toString().length() == 4));

        // ---------- BiFunction<T, U, R>: (T, U) -> R ----------
        BiFunction<Double, Integer, Double> lineTotal = (price, qty) -> price * qty;
        System.out.println("Line total = " + lineTotal.apply(49.5, 2));

        // ---------- UnaryOperator<T>: T -> T (special Function) ----------
        UnaryOperator<String> shout = s -> s.toUpperCase() + "!";
        System.out.println(shout.apply("sale today"));

        // ---------- BinaryOperator<T>: (T, T) -> T ----------
        BinaryOperator<Integer> bigger = (a, b) -> a > b ? a : b;
        System.out.println("Bigger of 7 and 12 = " + bigger.apply(7, 12));

        // ---------- Comparator<T>: (T, T) -> int ----------
        List<String> names = new ArrayList<>(List.of("Ravi", "anu", "Bala", "chitra"));
        Comparator<String> ignoreCase = (a, b) -> a.compareToIgnoreCase(b);
        names.sort(ignoreCase);
        System.out.println("Sorted names: " + names);

        // ---------- Putting them together: a tiny reusable "filter + print" ----------
        List<Integer> ages = List.of(12, 25, 17, 40, 18);
        printMatching(ages, isAdult, age -> System.out.println("  adult: " + age));
    }

    // A generic helper that receives TWO behaviours as parameters.
    // This is exactly the idea that Streams (next topic) are built on.
    static <T> void printMatching(List<T> items, Predicate<T> rule, Consumer<T> action) {
        System.out.println("Matching items:");
        for (T item : items) {
            if (rule.test(item)) {
                action.accept(item);
            }
        }
    }
}

/* Output:
Shop is open!
Is 20 adult? true
Is 15 adult? false
Length of 'Spring' = 6
SMS sent: Your order is on the way
New order id: ORD-1001
OTP is 4 digits? true
Line total = 99.0
SALE TODAY!
Bigger of 7 and 12 = 12
Sorted names: [anu, Bala, chitra, Ravi]
Matching items:
  adult: 25
  adult: 40
  adult: 18
*/
