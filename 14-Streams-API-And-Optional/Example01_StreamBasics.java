/*
 * Example01_StreamBasics.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   1. The same task done with a classic for-loop and with a stream.
 *   2. Ways to CREATE a stream (from a List, Stream.of, Arrays.stream).
 *   3. A stream PIPELINE: source -> intermediate ops -> terminal op.
 *   4. Streams are LAZY: nothing happens until a terminal operation.
 *   5. A stream can be used only ONCE.
 *   6. Streams never change the original list.
 *
 * HOW TO RUN:
 *   java Example01_StreamBasics.java
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Example01_StreamBasics {

    public static void main(String[] args) {
        List<String> names = List.of("Ravi", "Asha", "John", "Meena", "Arjun", "Bala");

        // ---------- 1. Classic loop: "names starting with A, in upper case" ----------
        List<String> loopResult = new ArrayList<>();
        for (String name : names) {
            if (name.startsWith("A")) {
                loopResult.add(name.toUpperCase());
            }
        }
        System.out.println("Loop   : " + loopResult);

        // ---------- Same thing with a stream ----------
        // Read it like a sentence: from names, keep those starting with A,
        // turn each into upper case, collect into a list.
        List<String> streamResult = names.stream()          // SOURCE
                .filter(name -> name.startsWith("A"))       // intermediate
                .map(String::toUpperCase)                   // intermediate
                .collect(Collectors.toList());              // TERMINAL
        System.out.println("Stream : " + streamResult);
        System.out.println("Original list is unchanged: " + names);

        // ---------- 2. Other ways to create a stream ----------
        Stream<String> fromValues = Stream.of("Pen", "Book", "Bag");
        System.out.println("Stream.of count = " + fromValues.count());

        int[] marks = {78, 92, 65};
        System.out.println("Arrays.stream sum = " + Arrays.stream(marks).sum());

        // ---------- 3/4. Laziness: watch WHEN each step runs ----------
        // peek() lets us look at elements as they flow (for learning/debugging).
        System.out.println("--- Building pipeline (no terminal op yet) ---");
        Stream<String> lazy = names.stream()
                .peek(n -> System.out.println("  looking at " + n))
                .filter(n -> n.length() > 4);
        System.out.println("--- Nothing printed above? Correct: streams are lazy! ---");

        System.out.println("--- Now calling the terminal op findFirst() ---");
        String firstLong = lazy.findFirst().orElse("none");
        System.out.println("First name longer than 4 letters: " + firstLong);
        // Notice it STOPPED at Meena - it did not look at Arjun and Bala.
        // Elements flow one by one through the WHOLE pipeline (not step by step).

        // ---------- 5. A stream can be consumed only once ----------
        Stream<String> once = names.stream();
        once.forEach(n -> { });            // uses it up
        try {
            once.count();                  // second use -> exception
        } catch (IllegalStateException e) {
            System.out.println("Second use fails: " + e.getMessage());
        }
    }
}

/* Output:
Loop   : [ASHA, ARJUN]
Stream : [ASHA, ARJUN]
Original list is unchanged: [Ravi, Asha, John, Meena, Arjun, Bala]
Stream.of count = 3
Arrays.stream sum = 235
--- Building pipeline (no terminal op yet) ---
--- Nothing printed above? Correct: streams are lazy! ---
--- Now calling the terminal op findFirst() ---
  looking at Ravi
  looking at Asha
  looking at John
  looking at Meena
First name longer than 4 letters: Meena
Second use fails: stream has already been operated upon or closed
*/
