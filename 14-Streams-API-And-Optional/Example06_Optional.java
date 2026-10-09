/*
 * Example06_Optional.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   Optional<T> - a box that either holds ONE value or is EMPTY.
 *   It forces the caller to think about "what if nothing was found?"
 *   instead of getting a surprise NullPointerException.
 *
 *   Creating : Optional.of, Optional.ofNullable, Optional.empty
 *   Checking : isPresent, isEmpty
 *   Getting  : orElse, orElseGet, orElseThrow
 *   Using    : ifPresent, ifPresentOrElse, map, filter
 *
 *   We also build a tiny fake "ProductRepository.findById" that returns
 *   Optional - EXACTLY like Spring Data JPA does (topic 38).
 *
 * HOW TO RUN:
 *   java Example06_Optional.java
 */
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Example06_Optional {

    static class Product {
        private final long id;
        private final String name;
        private final double price;

        Product(long id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        long getId() { return id; }
        String getName() { return name; }
        double getPrice() { return price; }

        @Override
        public String toString() { return name + " (Rs." + price + ")"; }
    }

    // Our own small exception, like the ones you'll create in Spring (topic 35)
    static class ProductNotFoundException extends RuntimeException {
        ProductNotFoundException(long id) {
            super("Product with id " + id + " not found");
        }
    }

    // A fake repository. Spring Data's JpaRepository has the same method signature:
    //     Optional<T> findById(ID id)
    static class ProductRepository {
        private final List<Product> products = List.of(
                new Product(1, "Keyboard", 1499),
                new Product(2, "Monitor", 8999),
                new Product(3, "USB Cable", 199));

        Optional<Product> findById(long id) {
            return products.stream()
                    .filter(p -> p.getId() == id)
                    .findFirst();                 // findFirst already returns Optional!
        }
    }

    public static void main(String[] args) {

        // ---------- Creating Optionals ----------
        Optional<String> city = Optional.of("Chennai");          // value MUST NOT be null
        Optional<String> nothing = Optional.empty();             // an empty box
        String maybeNull = null;
        Optional<String> safe = Optional.ofNullable(maybeNull);  // null -> empty box (no crash)
        System.out.println("of          : " + city);
        System.out.println("empty       : " + nothing);
        System.out.println("ofNullable  : " + safe);
        try {
            Optional.of(maybeNull);                               // null not allowed here!
        } catch (NullPointerException e) {
            System.out.println("of(null)    : NullPointerException - use ofNullable for maybe-null values");
        }

        // ---------- Checking ----------
        System.out.println("isPresent   : " + city.isPresent() + ", isEmpty: " + nothing.isEmpty());

        // ---------- Getting a value out ----------
        System.out.println("orElse      : " + nothing.orElse("Unknown city"));
        System.out.println("orElseGet   : " + nothing.orElseGet(() -> "Computed default"));
        try {
            nothing.orElseThrow();   // Java 10+: throws NoSuchElementException
        } catch (NoSuchElementException e) {
            System.out.println("orElseThrow : NoSuchElementException (" + e.getMessage() + ")");
        }

        // ---------- Using the value safely ----------
        city.ifPresent(c -> System.out.println("ifPresent   : delivering to " + c));
        nothing.ifPresentOrElse(
                c -> System.out.println("delivering to " + c),
                () -> System.out.println("ifPresentOrElse: no city given, asking user..."));

        // map: transform the value IF present (empty stays empty)
        System.out.println("map         : " + city.map(String::toUpperCase).orElse("-"));
        System.out.println("map (empty) : " + nothing.map(String::toUpperCase).orElse("-"));
        // filter: keep the value only if it passes a test
        System.out.println("filter      : " + city.filter(c -> c.startsWith("M")).orElse("not an M city"));

        // ---------- The REAL use: repository.findById ----------
        ProductRepository repo = new ProductRepository();

        // 1) Found -> use it
        Product monitor = repo.findById(2).orElseThrow(() -> new ProductNotFoundException(2));
        System.out.println("findById(2) : " + monitor);

        // 2) Chain: get only the NAME of product 3, or a default
        String name = repo.findById(3).map(Product::getName).orElse("No such product");
        System.out.println("name of 3   : " + name);

        // 3) Not found -> our own exception (in Spring this becomes HTTP 404)
        try {
            repo.findById(99).orElseThrow(() -> new ProductNotFoundException(99));
        } catch (ProductNotFoundException e) {
            System.out.println("findById(99): " + e.getMessage());
        }

        // 4) Optional in streams: max() returns Optional
        Optional<Product> cheapest = repo.products.stream()
                .min((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
        System.out.println("cheapest    : " + cheapest.map(Product::toString).orElse("none"));
    }
}

/* Output:
of          : Optional[Chennai]
empty       : Optional.empty
ofNullable  : Optional.empty
of(null)    : NullPointerException - use ofNullable for maybe-null values
isPresent   : true, isEmpty: true
orElse      : Unknown city
orElseGet   : Computed default
orElseThrow : NoSuchElementException (No value present)
ifPresent   : delivering to Chennai
ifPresentOrElse: no city given, asking user...
map         : CHENNAI
map (empty) : -
filter      : not an M city
findById(2) : Monitor (Rs.8999.0)
name of 3   : USB Cable
findById(99): Product with id 99 not found
cheapest    : USB Cable (Rs.199.0)
*/
