/*
 * Example06_TypeErasure.java
 *
 * What this file demonstrates - TYPE ERASURE in simple words:
 *   Generics are checked by the COMPILER, then the type arguments are ERASED.
 *   At runtime, List<String> and List<Integer> are both just "List" (ArrayList).
 *   Old Java code (before generics) keeps working - that was the reason for this design.
 *
 *   Consequences you can SEE here:
 *     1. getClass() is the same for ArrayList<String> and ArrayList<Integer>.
 *     2. You can't write  "x instanceof List<String>"  -> only  "x instanceof List<?>".
 *     3. You can't write  "new T()"  or  "new T[10]"  -> pass a Class<T> (or a factory) instead.
 *     4. Two overloads that differ only in the type argument CLASH:
 *          void save(List<String> a)  and  void save(List<Integer> b)  -> "name clash: same erasure"
 *     5. A static field can't use the class's T (there is one static field for ALL Box<...> types).
 *     6. Raw types defeat the compiler -> "heap pollution" -> ClassCastException far away.
 *
 * How to run:
 *   java Example06_TypeErasure.java
 *
 * Output:
 *   ArrayList<String>.getClass()  = class java.util.ArrayList
 *   ArrayList<Integer>.getClass() = class java.util.ArrayList
 *   Same class at runtime? true
 *   Is it a List<?>? true
 *   Created with Class<T>: Product{New product}
 *   Created with Class<T>: StringBuilder with text []
 *   Array of T: [null, null, null] length=3
 *   Heap pollution: a String got into a List<Integer>!
 *   ClassCastException when reading it as an Integer
 */
import java.util.ArrayList;
import java.util.List;

public class Example06_TypeErasure {

    public static void main(String[] args) throws Exception {
        // 1. Same class at runtime
        List<String> names = new ArrayList<>();
        List<Integer> numbers = new ArrayList<>();
        System.out.println("ArrayList<String>.getClass()  = " + names.getClass());
        System.out.println("ArrayList<Integer>.getClass() = " + numbers.getClass());
        System.out.println("Same class at runtime? " + (names.getClass() == numbers.getClass()));

        // 2. instanceof only with a wildcard
        Object something = names;
        // if (something instanceof List<String>) { }   // compile error: Object cannot be safely cast to List<String>
        System.out.println("Is it a List<?>? " + (something instanceof List<?>));

        // 3. "new T()" is impossible -> pass the Class object instead
        Factory<Product> productFactory = new Factory<>(Product.class);
        System.out.println("Created with Class<T>: " + productFactory.create());
        Factory<StringBuilder> sbFactory = new Factory<>(StringBuilder.class);
        System.out.println("Created with Class<T>: StringBuilder with text [" + sbFactory.create() + "]");

        // "new T[3]" is also impossible -> java.lang.reflect.Array or an Object[] with a cast
        String[] arr = ArrayMaker.make(String.class, 3);
        System.out.println("Array of T: " + java.util.Arrays.toString(arr) + " length=" + arr.length);

        // 6. Raw types switch the safety OFF
        List<Integer> marks = new ArrayList<>();
        addSomethingBad(marks);                     // the compiler only gives a warning in that method
        System.out.println("Heap pollution: a String got into a List<Integer>!");
        try {
            Integer first = marks.get(0);           // the hidden cast to Integer fails HERE, far from the bug
            System.out.println(first);
        } catch (ClassCastException e) {
            System.out.println("ClassCastException when reading it as an Integer");
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static void addSomethingBad(List list) {        // raw List parameter: no type checks at all
        list.add("ninety");
    }

    // 4. These two would NOT compile together - after erasure both are save(List):
    // static void save(List<String> names) { }
    // static void save(List<Integer> ids) { }
    //   error: name clash: save(List<Integer>) and save(List<String>) have the same erasure
}

class Factory<T> {
    private final Class<T> type;    // the "type token": a runtime object that remembers T

    Factory(Class<T> type) {
        this.type = type;
    }

    T create() throws Exception {
        // T t = new T();            // compile error: T is erased - Java doesn't know what to create
        return type.getDeclaredConstructor().newInstance();   // reflection (Topic 18) knows the real class
    }
}

class ArrayMaker {
    @SuppressWarnings("unchecked")
    static <T> T[] make(Class<T> type, int size) {
        return (T[]) java.lang.reflect.Array.newInstance(type, size);
    }
}

class Product {
    private final String name;

    public Product() {               // needed by Factory: a public no-argument constructor
        this.name = "New product";
    }

    @Override
    public String toString() {
        return "Product{" + name + "}";
    }
}

// 5. static fields can't use T:
class Counter<T> {
    // static T lastValue;          // compile error: non-static type variable T cannot be referenced from a static context
    static int instancesCreated;    // fine: ONE counter shared by Counter<String>, Counter<Integer>, ...
}
