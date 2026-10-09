/*
 * Example05_EffectivelyFinalAndComposition.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   PART A - "effectively final": a lambda can READ local variables
 *            from the surrounding method, but only if they are never
 *            changed after being set.
 *   PART B - Composing (joining) small lambdas into bigger ones:
 *            Function.andThen / Function.compose / Function.identity
 *            Predicate.and / or / negate / Predicate.not
 *            Consumer.andThen
 *            Comparator.comparing / thenComparing / reversed
 *
 * HOW TO RUN:
 *   java Example05_EffectivelyFinalAndComposition.java
 */
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Example05_EffectivelyFinalAndComposition {

    // Simple Product class for the Online Shop examples.
    static class Product {
        private final String name;
        private final String category;
        private final double price;

        Product(String name, String category, double price) {
            this.name = name;
            this.category = category;
            this.price = price;
        }

        String getName() { return name; }
        String getCategory() { return category; }
        double getPrice() { return price; }

        @Override
        public String toString() {
            return name + "(" + category + ", " + price + ")";
        }
    }

    // A field (instance or static) CAN be changed inside a lambda -
    // the "effectively final" rule is only for LOCAL variables.
    static int clickCounter = 0;

    public static void main(String[] args) {

        // ================= PART A: effectively final =================
        double taxRate = 0.18;   // never changed again -> "effectively final"
        Function<Double, Double> addTax = price -> price + price * taxRate;  // OK: only READS taxRate
        System.out.println("A) 100 + tax = " + addTax.apply(100.0));

        // taxRate = 0.12;   // <-- if you un-comment this line, the lambda above
        //                   //     stops compiling: "local variables referenced from
        //                   //     a lambda expression must be final or effectively final"

        Runnable click = () -> clickCounter++;   // static FIELD: allowed
        click.run();
        click.run();
        System.out.println("A) clicks = " + clickCounter);

        // ================= PART B: composing Functions =================
        Function<Double, Double> applyDiscount = price -> price - 100;  // 100 off
        Function<Double, Double> applyGst = price -> price * 1.18;      // 18% tax

        // andThen: FIRST this, THEN that   -> discount, then tax
        Function<Double, Double> discountThenTax = applyDiscount.andThen(applyGst);
        // compose: FIRST the argument, THEN this -> tax, then discount
        Function<Double, Double> taxThenDiscount = applyDiscount.compose(applyGst);

        System.out.println("B) discount then tax on 1000 = " + discountThenTax.apply(1000.0));
        System.out.println("B) tax then discount on 1000 = " + taxThenDiscount.apply(1000.0));

        Function<String, String> same = Function.identity();   // returns its input unchanged
        System.out.println("B) identity: " + same.apply("unchanged"));

        // ================= Composing Predicates =================
        List<Product> products = new ArrayList<>(List.of(
                new Product("Laptop", "Electronics", 55000),
                new Product("Mouse", "Electronics", 499),
                new Product("Rice 5kg", "Grocery", 350),
                new Product("Headphones", "Electronics", 1999),
                new Product("Olive Oil", "Grocery", 899)));

        Predicate<Product> isElectronics = p -> p.getCategory().equals("Electronics");
        Predicate<Product> isCheap = p -> p.getPrice() < 1000;

        System.out.println("C) Cheap electronics   : " + filter(products, isElectronics.and(isCheap)));
        System.out.println("C) Electronics OR cheap: " + filter(products, isElectronics.or(isCheap)));
        System.out.println("C) NOT electronics     : " + filter(products, isElectronics.negate()));
        System.out.println("C) NOT cheap (not())   : " + filter(products, Predicate.not(isCheap)));

        // ================= Composing Consumers =================
        Consumer<String> log = msg -> System.out.println("D) LOG  : " + msg);
        Consumer<String> email = msg -> System.out.println("D) EMAIL: " + msg);
        log.andThen(email).accept("Order #77 shipped");   // runs log, then email

        // ================= Comparator helpers =================
        // comparing(keyExtractor) builds a comparator from a "key" (here: price)
        products.sort(Comparator.comparing(Product::getPrice));
        System.out.println("E) by price        : " + products);

        // by category, then by price highest first
        products.sort(Comparator.comparing(Product::getCategory)
                .thenComparing(Product::getPrice, Comparator.reverseOrder()));
        System.out.println("E) category, price desc: " + products);

        products.sort(Comparator.comparing(Product::getName).reversed());
        System.out.println("E) name reversed   : " + products);
    }

    // Generic helper: keep only items that pass the rule.
    static <T> List<T> filter(List<T> items, Predicate<T> rule) {
        List<T> result = new ArrayList<>();
        for (T item : items) {
            if (rule.test(item)) {
                result.add(item);
            }
        }
        return result;
    }
}

/* Output:
A) 100 + tax = 118.0
A) clicks = 2
B) discount then tax on 1000 = 1062.0
B) tax then discount on 1000 = 1080.0
B) identity: unchanged
C) Cheap electronics   : [Mouse(Electronics, 499.0)]
C) Electronics OR cheap: [Laptop(Electronics, 55000.0), Mouse(Electronics, 499.0), Rice 5kg(Grocery, 350.0), Headphones(Electronics, 1999.0), Olive Oil(Grocery, 899.0)]
C) NOT electronics     : [Rice 5kg(Grocery, 350.0), Olive Oil(Grocery, 899.0)]
C) NOT cheap (not())   : [Laptop(Electronics, 55000.0), Headphones(Electronics, 1999.0)]
D) LOG  : Order #77 shipped
D) EMAIL: Order #77 shipped
E) by price        : [Rice 5kg(Grocery, 350.0), Mouse(Electronics, 499.0), Olive Oil(Grocery, 899.0), Headphones(Electronics, 1999.0), Laptop(Electronics, 55000.0)]
E) category, price desc: [Laptop(Electronics, 55000.0), Headphones(Electronics, 1999.0), Mouse(Electronics, 499.0), Olive Oil(Grocery, 899.0), Rice 5kg(Grocery, 350.0)]
E) name reversed   : [Rice 5kg(Grocery, 350.0), Olive Oil(Grocery, 899.0), Mouse(Electronics, 499.0), Laptop(Electronics, 55000.0), Headphones(Electronics, 1999.0)]
*/
