/*
 * Example05_Wildcards.java
 *
 * What this file demonstrates:
 *   - The SURPRISE: List<Integer> is NOT a List<Number>, even though Integer IS a Number.
 *     (Generics are "invariant".) So a method taking List<Number> refuses a List<Integer>.
 *   - Wildcards ("?") fix this for method PARAMETERS:
 *       List<?>               unknown type    -> read as Object only, can't add (except null)
 *       List<? extends Number> Number or a subtype -> safe to READ Numbers, can't add
 *       List<? super Integer>  Integer or a supertype -> safe to ADD Integers, reads give Object
 *   - PECS: "Producer Extends, Consumer Super"
 *       a list you READ FROM (it produces values)   -> ? extends
 *       a list you WRITE INTO (it consumes values)   -> ? super
 *   - The classic example: copy(source, destination) - just like java.util.Collections.copy.
 *
 * Domain: an animal shelter (Animal > Dog, Cat) and numbers.
 *
 * How to run:
 *   java Example05_Wildcards.java
 *
 * Output:
 *   --- List<?> (unknown type) ---
 *   3 item(s): Pen 42 true
 *   --- ? extends (read / producer) ---
 *   Total of Integers: 60.0
 *   Total of Doubles: 3.75
 *   Feeding: Bruno the Dog
 *   Feeding: Tommy the Dog
 *   Feeding: Kitty the Cat
 *   --- ? super (write / consumer) ---
 *   Integer list: [1, 2, 3]
 *   Number list : [1, 2, 3]
 *   Object list : [start, 1, 2, 3]
 *   --- PECS copy ---
 *   Copied dogs into the animal list: [Bruno the Dog, Tommy the Dog]
 */
import java.util.ArrayList;
import java.util.List;

public class Example05_Wildcards {

    // ---------- unbounded wildcard: "a list of SOMETHING, I don't care what" ----------
    static void printAnything(List<?> items) {
        StringBuilder sb = new StringBuilder(items.size() + " item(s):");
        for (Object o : items) {          // all we know: each element is at least an Object
            sb.append(" ").append(o);
        }
        System.out.println(sb);
        // items.add("x");                // compile error: we don't know the real type, so adding is unsafe
    }

    // ---------- upper bounded wildcard: PRODUCER - we only READ from it ----------
    static double total(List<? extends Number> numbers) {
        double sum = 0;
        for (Number n : numbers) {        // every element is guaranteed to be some kind of Number
            sum += n.doubleValue();
        }
        // numbers.add(5);                // compile error: it might really be a List<Double>!
        return sum;
    }

    static void feedAll(List<? extends Animal> animals) {
        for (Animal a : animals) {
            System.out.println("Feeding: " + a);
        }
    }

    // ---------- lower bounded wildcard: CONSUMER - we only WRITE into it ----------
    static void addOneTwoThree(List<? super Integer> target) {
        target.add(1);                    // safe: the list holds Integer or a parent of Integer
        target.add(2);
        target.add(3);
        // Integer x = target.get(0);     // compile error: it might be a List<Object>; reads give Object
        Object first = target.get(0);     // reading as Object is fine
        if (first == null) {
            System.out.println("never");
        }
    }

    // ---------- PECS in one method ----------
    // source PRODUCES T's (extends), destination CONSUMES T's (super)
    static <T> void copy(List<? extends T> source, List<? super T> destination) {
        for (T item : source) {
            destination.add(item);
        }
    }

    public static void main(String[] args) {
        System.out.println("--- List<?> (unknown type) ---");
        List<Object> mixed = List.of("Pen", 42, true);
        printAnything(mixed);

        System.out.println("--- ? extends (read / producer) ---");
        List<Integer> quantities = List.of(10, 20, 30);
        List<Double> weights = List.of(1.25, 2.5);
        System.out.println("Total of Integers: " + total(quantities));   // works: Integer extends Number
        System.out.println("Total of Doubles: " + total(weights));      // works: Double extends Number

        // Why do we need the wildcard? Try this (it does NOT compile):
        //   List<Number> nums = quantities;   // incompatible types: List<Integer> cannot be converted to List<Number>
        // If it were allowed, someone could do nums.add(3.14) and put a Double into a list of Integers!

        List<Dog> dogs = List.of(new Dog("Bruno"), new Dog("Tommy"));
        List<Cat> cats = List.of(new Cat("Kitty"));
        feedAll(dogs);
        feedAll(cats);

        System.out.println("--- ? super (write / consumer) ---");
        List<Integer> ints = new ArrayList<>();
        List<Number> numbers = new ArrayList<>();
        List<Object> objects = new ArrayList<>();
        objects.add("start");
        addOneTwoThree(ints);       // Integer is "super" of Integer (itself)
        addOneTwoThree(numbers);    // Number is a parent of Integer
        addOneTwoThree(objects);    // Object is a parent of everything
        // addOneTwoThree(new ArrayList<Double>());   // compile error: Double is not a parent of Integer
        System.out.println("Integer list: " + ints);
        System.out.println("Number list : " + numbers);
        System.out.println("Object list : " + objects);

        System.out.println("--- PECS copy ---");
        List<Animal> shelter = new ArrayList<>();
        copy(dogs, shelter);        // read from List<Dog>, write into List<Animal>
        System.out.println("Copied dogs into the animal list: " + shelter);
    }
}

class Animal {
    private final String name;
    Animal(String name) { this.name = name; }
    String getName() { return name; }
}

class Dog extends Animal {
    Dog(String name) { super(name); }
    @Override public String toString() { return getName() + " the Dog"; }
}

class Cat extends Animal {
    Cat(String name) { super(name); }
    @Override public String toString() { return getName() + " the Cat"; }
}
