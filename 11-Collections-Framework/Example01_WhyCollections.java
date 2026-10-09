/*
 * Example01_WhyCollections.java
 *
 * What this file demonstrates:
 *   - The pain of ARRAYS: fixed size, no easy add/remove/search, manual shifting.
 *   - The same job with an ArrayList: grows automatically, has ready-made methods.
 *   - Generics in one sentence: List<String> means "a list that holds only Strings"
 *     (full story in Topic 12).
 *   - Collections hold OBJECTS, so int becomes Integer (autoboxing, Topic 2).
 *
 * Domain: a shopping cart.
 *
 * How to run:
 *   java Example01_WhyCollections.java
 *
 * Output:
 *   --- With an array ---
 *   Cart is full! Cannot add: Soap
 *   Array cart: [Rice, Milk, Bread]
 *   After removing Milk by hand: [Rice, Bread, null]
 *   --- With an ArrayList ---
 *   List cart: [Rice, Milk, Bread, Soap, Eggs]
 *   Size: 5
 *   Contains Milk? true
 *   Position of Bread: 2
 *   After remove("Milk"): [Rice, Bread, Soap, Eggs]
 *   After add(0, "Tea"): [Tea, Rice, Bread, Soap, Eggs]
 *   Item at index 1: Rice
 *   --- Numbers in a list (autoboxing) ---
 *   Quantities: [2, 1, 5]  total = 8
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Example01_WhyCollections {

    public static void main(String[] args) {
        System.out.println("--- With an array ---");
        String[] cartArray = new String[3];          // size fixed FOREVER at creation
        cartArray[0] = "Rice";
        cartArray[1] = "Milk";
        cartArray[2] = "Bread";
        int count = 3;
        if (count == cartArray.length) {
            // To add a 4th item we would have to create a NEW bigger array and copy everything.
            System.out.println("Cart is full! Cannot add: Soap");
        }
        System.out.println("Array cart: " + Arrays.toString(cartArray));

        // Removing "Milk" means shifting every later element left by hand:
        for (int i = 1; i < count - 1; i++) {
            cartArray[i] = cartArray[i + 1];
        }
        cartArray[count - 1] = null;
        count--;
        System.out.println("After removing Milk by hand: " + Arrays.toString(cartArray));

        System.out.println("--- With an ArrayList ---");
        // List<String>  = the TYPE of the variable (an interface: "some kind of list of Strings")
        // new ArrayList<>() = the actual object. The <> ("diamond") copies <String> from the left side.
        List<String> cart = new ArrayList<>();
        cart.add("Rice");
        cart.add("Milk");
        cart.add("Bread");
        cart.add("Soap");          // no "full" problem - it grows automatically
        cart.add("Eggs");
        System.out.println("List cart: " + cart);   // nice toString() for free
        System.out.println("Size: " + cart.size());
        System.out.println("Contains Milk? " + cart.contains("Milk"));
        System.out.println("Position of Bread: " + cart.indexOf("Bread"));

        cart.remove("Milk");       // shifting is done for us
        System.out.println("After remove(\"Milk\"): " + cart);

        cart.add(0, "Tea");        // insert at a position
        System.out.println("After add(0, \"Tea\"): " + cart);
        System.out.println("Item at index 1: " + cart.get(1));

        // cart.add(42);   // compile error: the list only accepts Strings - that's TYPE SAFETY

        System.out.println("--- Numbers in a list (autoboxing) ---");
        // Collections can't hold primitives (int), only objects (Integer).
        // Java converts automatically: 2 -> Integer.valueOf(2). This is called autoboxing.
        List<Integer> quantities = new ArrayList<>();
        quantities.add(2);
        quantities.add(1);
        quantities.add(5);
        int total = 0;
        for (int q : quantities) {   // unboxing: Integer -> int
            total += q;
        }
        System.out.println("Quantities: " + quantities + "  total = " + total);
    }
}
