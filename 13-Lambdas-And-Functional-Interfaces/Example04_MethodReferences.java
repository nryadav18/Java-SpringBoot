/*
 * Example04_MethodReferences.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   Method references ( :: ) - an even SHORTER lambda, used when the
 *   lambda does nothing except call ONE existing method.
 *
 *   The 4 kinds:
 *   1. Static method               ClassName::staticMethod
 *   2. Method of a particular obj  object::instanceMethod
 *   3. Method of an arbitrary obj  ClassName::instanceMethod
 *   4. Constructor                 ClassName::new
 *
 * HOW TO RUN:
 *   java Example04_MethodReferences.java
 */
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Example04_MethodReferences {

    // A small class from a Student system, used for the constructor reference.
    static class Student {
        private final String name;

        Student(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Student(" + name + ")";
        }
    }

    // A static helper method we will point to with a method reference.
    static boolean isPassMark(int marks) {
        return marks >= 35;
    }

    public static void main(String[] args) {

        // ---------- 1. Reference to a STATIC method ----------
        // Lambda:            s -> Integer.parseInt(s)
        // Method reference:  Integer::parseInt
        Function<String, Integer> parseLambda = s -> Integer.parseInt(s);
        Function<String, Integer> parseRef = Integer::parseInt;
        System.out.println("1) static      : " + parseLambda.apply("10") + " and " + parseRef.apply("20"));

        // Our own static method works the same way:
        java.util.function.Predicate<Integer> passed = Example04_MethodReferences::isPassMark;
        System.out.println("   70 passed?  : " + passed.test(70));

        // ---------- 2. Instance method of a PARTICULAR object ----------
        // We already HAVE the object (System.out) and call its method.
        // Lambda:            msg -> System.out.println(msg)
        // Method reference:  System.out::println
        Consumer<String> printer = System.out::println;
        printer.accept("2) particular  : printed via System.out::println");

        String shopName = "FreshMart";
        Supplier<Integer> nameLength = shopName::length;   // object is 'shopName'
        System.out.println("   length of FreshMart = " + nameLength.get());

        // ---------- 3. Instance method of an ARBITRARY object of a type ----------
        // The object is NOT known yet; it is the FIRST parameter of the lambda.
        // Lambda:            s -> s.toUpperCase()
        // Method reference:  String::toUpperCase
        Function<String, String> upper = String::toUpperCase;
        System.out.println("3) arbitrary   : " + upper.apply("lambda"));

        // With two parameters: (a, b) -> a.compareToIgnoreCase(b)
        List<String> cities = new ArrayList<>(List.of("pune", "Delhi", "agra", "Chennai"));
        cities.sort(String::compareToIgnoreCase);
        System.out.println("   sorted      : " + cities);

        // ---------- 4. Reference to a CONSTRUCTOR ----------
        // Lambda:            name -> new Student(name)
        // Method reference:  Student::new
        Function<String, Student> makeStudent = Student::new;
        System.out.println("4) constructor : " + makeStudent.apply("Asha"));

        Supplier<List<String>> newList = ArrayList::new;    // () -> new ArrayList<>()
        List<String> empty = newList.get();
        System.out.println("   new list    : " + empty + " size=" + empty.size());

        BiFunction<String, Integer, String> repeat = String::repeat; // (s, n) -> s.repeat(n)
        System.out.println("   repeat      : " + repeat.apply("ab", 3));

        // ---------- Using method references with forEach ----------
        System.out.println("All cities:");
        cities.forEach(System.out::println);
    }
}

/* Output:
1) static      : 10 and 20
   70 passed?  : true
2) particular  : printed via System.out::println
   length of FreshMart = 9
3) arbitrary   : LAMBDA
   sorted      : [agra, Chennai, Delhi, pune]
4) constructor : Student(Asha)
   new list    : [] size=0
   repeat      : ababab
All cities:
agra
Chennai
Delhi
pune
*/
