/*
 * Example08_CollectionsUtilityAndImmutable.java
 *
 * What this file demonstrates:
 *   - java.util.Collections (with an "s") - a utility class full of static helpers:
 *       sort, reverse, shuffle (with a fixed seed), max, min, frequency, swap,
 *       nCopies, addAll, unmodifiableList, emptyList
 *   - Immutable collections (Java 9+): List.of, Set.of, Map.of, List.copyOf
 *       -> any change throws UnsupportedOperationException
 *       -> they do NOT allow null
 *   - The difference between an UNMODIFIABLE VIEW and a true COPY.
 *   - Arrays.asList: fixed-size list backed by an array (a common surprise).
 *
 * How to run:
 *   java Example08_CollectionsUtilityAndImmutable.java
 *
 * Output:
 *   Original : [40, 10, 30, 20, 10]
 *   sort     : [10, 10, 20, 30, 40]
 *   reverse  : [40, 30, 20, 10, 10]
 *   max=40 min=10 frequency(10)=2
 *   swap(0,4): [10, 30, 20, 10, 40]
 *   shuffle(seed 42): [30, 20, 10, 40, 10]
 *   nCopies  : [-, -, -, -, -]
 *   addAll   : [Mon, Tue, Wed]
 *   --- Immutable collections ---
 *   List.of : [Red, Green, Blue]
 *   Cannot add to List.of -> UnsupportedOperationException
 *   List.of(null) -> NullPointerException
 *   Map.of  : Mon=9am-5pm
 *   --- View vs copy ---
 *   view : [A, B, C]   copy : [A, B]
 *   view.add() -> UnsupportedOperationException
 *   --- Arrays.asList ---
 *   [x, y, z] -> set(0) works: [X, y, z]
 *   asList.add() -> UnsupportedOperationException (fixed size)
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Example08_CollectionsUtilityAndImmutable {

    public static void main(String[] args) {
        List<Integer> nums = new ArrayList<>(List.of(40, 10, 30, 20, 10));
        System.out.println("Original : " + nums);
        Collections.sort(nums);
        System.out.println("sort     : " + nums);
        Collections.reverse(nums);
        System.out.println("reverse  : " + nums);
        System.out.println("max=" + Collections.max(nums) + " min=" + Collections.min(nums)
                + " frequency(10)=" + Collections.frequency(nums, 10));
        Collections.swap(nums, 0, 4);
        System.out.println("swap(0,4): " + nums);
        Collections.shuffle(nums, new Random(42));    // fixed seed -> same "random" order every run
        System.out.println("shuffle(seed 42): " + nums);
        System.out.println("nCopies  : " + Collections.nCopies(5, "-"));
        List<String> days = new ArrayList<>();
        Collections.addAll(days, "Mon", "Tue", "Wed");
        System.out.println("addAll   : " + days);

        System.out.println("--- Immutable collections ---");
        List<String> colours = List.of("Red", "Green", "Blue");   // cannot be changed - ever
        System.out.println("List.of : " + colours);
        try {
            colours.add("Pink");
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot add to List.of -> UnsupportedOperationException");
        }
        try {
            List.of("A", null);
        } catch (NullPointerException e) {
            System.out.println("List.of(null) -> NullPointerException");
        }
        Map<String, String> hours = Map.of("Mon", "9am-5pm");   // up to 10 key/value pairs
        System.out.println("Map.of  : Mon=" + hours.get("Mon"));

        System.out.println("--- View vs copy ---");
        List<String> original = new ArrayList<>(List.of("A", "B"));
        List<String> view = Collections.unmodifiableList(original);   // read-only WINDOW onto original
        List<String> copy = List.copyOf(original);                     // independent frozen COPY
        original.add("C");                                             // change the original...
        System.out.println("view : " + view + "   copy : " + copy);   // ...the view sees it, the copy doesn't
        try {
            view.add("D");
        } catch (UnsupportedOperationException e) {
            System.out.println("view.add() -> UnsupportedOperationException");
        }

        System.out.println("--- Arrays.asList ---");
        List<String> letters = Arrays.asList("x", "y", "z");   // backed by an array: size is FIXED
        System.out.print(letters + " -> set(0) works: ");
        letters.set(0, "X");
        System.out.println(letters);
        try {
            letters.add("w");
        } catch (UnsupportedOperationException e) {
            System.out.println("asList.add() -> UnsupportedOperationException (fixed size)");
        }
        // Need a normal, growable list?  new ArrayList<>(Arrays.asList(...))  or  new ArrayList<>(List.of(...))
    }
}
