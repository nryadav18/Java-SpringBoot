/*
 * Example03_GenericMethods.java
 *
 * What this file demonstrates:
 *   - A GENERIC METHOD declares its OWN type parameter, written BEFORE the return type:
 *         static <T> void printAll(List<T> items)
 *   - The class itself does NOT need to be generic.
 *   - Type INFERENCE: Java figures out T from the arguments - you rarely write it.
 *     (Explicit form when needed:  Example03_GenericMethods.<String>firstOrDefault(...))
 *   - Generic methods that RETURN T (no cast for the caller).
 *   - A generic method with two type parameters: <K, V>
 *   - The JDK is full of them: List.of, Collections.max, Arrays.asList, Map.of ...
 *
 * How to run:
 *   java Example03_GenericMethods.java
 *
 * Output:
 *   [1] Pen  [2] Book  [3] Bag
 *   [1] 10  [2] 20  [3] 30
 *   First city: Pune
 *   First of empty list: (none)
 *   Last price: 99.5
 *   Before swap: [A, B, C]  after swap(0, 2): [C, B, A]
 *   Repeated: [Hi, Hi, Hi]
 *   Map from two lists: {Asha=91, Ravi=85, Meena=78}
 *   Explicit type argument: hello
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Example03_GenericMethods {

    // <T> = "this method works for any type T". The caller decides T.
    static <T> void printAll(List<T> items) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            sb.append("[").append(i + 1).append("] ").append(items.get(i)).append("  ");
        }
        System.out.println(sb.toString().trim());
    }

    // Returns T: whatever type the list holds, you get that type back - no cast.
    static <T> T firstOrDefault(List<T> items, T defaultValue) {
        return items.isEmpty() ? defaultValue : items.get(0);
    }

    static <T> T last(T[] array) {
        return array[array.length - 1];
    }

    // Works on an array of ANY object type
    static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    static <T> List<T> repeat(T value, int times) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            result.add(value);
        }
        return result;
    }

    // Two type parameters
    static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
        Map<K, V> map = new LinkedHashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }
        return map;
    }

    public static void main(String[] args) {
        printAll(List.of("Pen", "Book", "Bag"));          // T inferred as String
        printAll(List.of(10, 20, 30));                     // T inferred as Integer

        String city = firstOrDefault(List.of("Pune", "Delhi"), "(none)");
        System.out.println("First city: " + city);
        List<String> empty = new ArrayList<>();
        System.out.println("First of empty list: " + firstOrDefault(empty, "(none)"));

        Double[] prices = {49.0, 10.25, 99.5};             // generics need objects: Double[], not double[]
        double lastPrice = last(prices);
        System.out.println("Last price: " + lastPrice);

        String[] letters = {"A", "B", "C"};
        String before = Arrays.toString(letters);
        swap(letters, 0, 2);
        System.out.println("Before swap: " + before + "  after swap(0, 2): " + Arrays.toString(letters));

        System.out.println("Repeated: " + repeat("Hi", 3));

        Map<String, Integer> marks = zipToMap(List.of("Asha", "Ravi", "Meena"), List.of(91, 85, 78));
        System.out.println("Map from two lists: " + marks);

        // Rarely needed: tell Java the type explicitly (ClassName.<Type>method(...))
        String s = Example03_GenericMethods.<String>firstOrDefault(List.of("hello"), "x");
        System.out.println("Explicit type argument: " + s);
    }
}
