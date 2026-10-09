/*
 * Example06_NpeCollectionsAndStrings.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   PART A - Helpful NullPointerExceptions (Java 14+, on by default in 17):
 *            the error message tells you EXACTLY what was null.
 *   PART B - Collection factory methods (Java 9/10):
 *            List.of, Set.of, Map.of, Map.ofEntries, List.copyOf
 *            -> immutable, no nulls allowed.
 *   PART C - Newer String methods (Java 11-15):
 *            isBlank, strip/stripLeading/stripTrailing, lines, repeat,
 *            indent, transform, formatted, chars
 *
 * HOW TO RUN:
 *   java Example06_NpeCollectionsAndStrings.java
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Example06_NpeCollectionsAndStrings {

    // Small classes to create a chain of calls: order -> customer -> address -> city
    static class Address {
        private final String city;
        Address(String city) { this.city = city; }
        String getCity() { return city; }
    }

    static class Customer {
        private final Address address;
        Customer(Address address) { this.address = address; }
        Address getAddress() { return address; }
    }

    static class Order {
        private final Customer customer;
        Order(Customer customer) { this.customer = customer; }
        Customer getCustomer() { return customer; }
    }

    static String cityOf(Order order) {
        return order.getCustomer().getAddress().getCity().toUpperCase();
    }

    public static void main(String[] args) {

        // ================= PART A: helpful NPE =================
        Order ok = new Order(new Customer(new Address("Pune")));
        System.out.println("A) city: " + cityOf(ok));

        Order noAddress = new Order(new Customer(null));   // customer has no address!
        try {
            cityOf(noAddress);
        } catch (NullPointerException e) {
            // Before Java 14 the message was just "null" - you had to guess
            // which of the 4 calls in that line failed. Now Java tells you:
            System.out.println("A) NPE message: " + e.getMessage());
        }

        // ================= PART B: collection factories =================
        List<String> fruits = List.of("Apple", "Mango", "Banana");
        Set<Integer> primes = Set.of(2, 3, 5, 7);
        Map<String, Integer> stock = Map.of("Pen", 100, "Book", 25);   // up to 10 pairs
        Map<String, String> capitals = Map.ofEntries(                   // any number of pairs
                Map.entry("India", "New Delhi"),
                Map.entry("Japan", "Tokyo"));

        System.out.println("B) fruits  : " + fruits);
        System.out.println("B) primes has 5? " + primes.contains(5) + ", size " + primes.size());
        System.out.println("B) stock   : " + new TreeMap<>(stock) + "  (TreeMap just to print sorted)");
        System.out.println("B) capitals: " + new TreeMap<>(capitals));

        // They are IMMUTABLE (unmodifiable)
        try {
            fruits.add("Grapes");
        } catch (UnsupportedOperationException e) {
            System.out.println("B) List.of(...).add -> UnsupportedOperationException");
        }

        // They do NOT allow null
        try {
            List.of("A", null);
        } catch (NullPointerException e) {
            System.out.println("B) List.of with null -> NullPointerException");
        }

        // Set.of / Map.of reject duplicates at creation
        try {
            Set.of("x", "x");
        } catch (IllegalArgumentException e) {
            System.out.println("B) Set.of duplicates -> IllegalArgumentException");
        }

        // copyOf: make an immutable snapshot of a mutable list
        List<String> cart = new ArrayList<>(List.of("Milk"));
        List<String> snapshot = List.copyOf(cart);
        cart.add("Eggs");
        System.out.println("B) cart=" + cart + " snapshot=" + snapshot);

        // ================= PART C: String methods =================
        String blank = "   ";
        System.out.println("C) isEmpty/isBlank : " + blank.isEmpty() + " / " + blank.isBlank());

        char emSpace = (char) 0x2003;                     // a Unicode "em space" (a wide space character)
        String messy = emSpace + "  Hello Java  ";
        // The Windows console usually shows the em space as '?'
        System.out.println("C) trim()          : [" + messy.trim() + "]  (trim misses Unicode spaces)");
        System.out.println("C) strip()         : [" + messy.strip() + "]");
        System.out.println("C) stripLeading()  : [" + "  hi  ".stripLeading() + "]");
        System.out.println("C) stripTrailing() : [" + "  hi  ".stripTrailing() + "]");

        System.out.println("C) repeat          : " + "=-".repeat(5));

        String address = "12 MG Road\nBengaluru\n560001";
        System.out.println("C) lines().count() : " + address.lines().count());
        System.out.println("C) lines joined    : " + address.lines().collect(Collectors.joining(", ")));

        System.out.print("C) indent(4):\n" + "line1\nline2".indent(4));

        // transform: apply any function to the string (handy for chaining)
        String slug = "  Spring Boot Tutorial  ".transform(s -> s.strip().toLowerCase().replace(' ', '-'));
        System.out.println("C) transform       : " + slug);

        System.out.println("C) formatted       : " + "%s scored %d%%".formatted("Asha", 95));

        long upperCount = "Java Spring Boot".chars().filter(Character::isUpperCase).count();
        System.out.println("C) chars() upper   : " + upperCount);
    }
}

/* Output:
A) city: PUNE
A) NPE message: Cannot invoke "Example06_NpeCollectionsAndStrings$Address.getCity()" because the return value of "Example06_NpeCollectionsAndStrings$Customer.getAddress()" is null
B) fruits  : [Apple, Mango, Banana]
B) primes has 5? true, size 4
B) stock   : {Book=25, Pen=100}  (TreeMap just to print sorted)
B) capitals: {India=New Delhi, Japan=Tokyo}
B) List.of(...).add -> UnsupportedOperationException
B) List.of with null -> NullPointerException
B) Set.of duplicates -> IllegalArgumentException
B) cart=[Milk, Eggs] snapshot=[Milk]
C) isEmpty/isBlank : false / true
C) trim()          : [?  Hello Java]  (trim misses Unicode spaces)
C) strip()         : [Hello Java]
C) stripLeading()  : [hi  ]
C) stripTrailing() : [  hi]
C) repeat          : =-=-=-=-=-
C) lines().count() : 3
C) lines joined    : 12 MG Road, Bengaluru, 560001
C) indent(4):
    line1
    line2
C) transform       : spring-boot-tutorial
C) formatted       : Asha scored 95%
C) chars() upper   : 3
*/
