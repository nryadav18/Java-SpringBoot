/*
 * Example02_LambdaSyntax.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   1. What a FUNCTIONAL INTERFACE is (an interface with exactly ONE
 *      abstract method) and the @FunctionalInterface annotation.
 *   2. All the different ways you can WRITE a lambda:
 *      - no parameters, one parameter, many parameters
 *      - with / without parameter types
 *      - expression body vs block body { ... return ...; }
 *
 * HOW TO RUN:
 *   java Example02_LambdaSyntax.java
 */
public class Example02_LambdaSyntax {

    // Our OWN functional interfaces (one abstract method each).
    // @FunctionalInterface is optional, but it asks the compiler to
    // CHECK that there really is only one abstract method.
    @FunctionalInterface
    interface Greeter {
        void greet();                       // no input, no output
    }

    @FunctionalInterface
    interface DiscountRule {
        double apply(double price);         // one input, one output
    }

    @FunctionalInterface
    interface BillCalculator {
        double total(double price, int quantity);   // two inputs
    }

    public static void main(String[] args) {

        // 1) No parameters -> empty brackets ()
        Greeter hello = () -> System.out.println("Welcome to the Online Shop!");
        hello.greet();

        // 2) One parameter -> brackets are OPTIONAL
        DiscountRule tenPercentOff = price -> price * 0.90;
        DiscountRule flat50Off = (price) -> price - 50;       // same thing with ()
        System.out.println("10% off 1000  = " + tenPercentOff.apply(1000));
        System.out.println("Flat 50 off   = " + flat50Off.apply(1000));

        // 3) Explicit parameter type (allowed, usually not needed)
        DiscountRule festival = (double price) -> price * 0.75;
        System.out.println("Festival 25%  = " + festival.apply(1000));

        // 4) Two parameters -> brackets are REQUIRED
        BillCalculator simple = (price, qty) -> price * qty;
        System.out.println("3 x 199.0     = " + simple.total(199.0, 3));

        // 5) Block body: use { } when you need several statements.
        //    In a block body you MUST write 'return' yourself.
        BillCalculator withTax = (price, qty) -> {
            double subtotal = price * qty;
            double tax = subtotal * 0.18;   // 18% GST
            return subtotal + tax;
        };
        System.out.println("With 18% tax  = " + withTax.total(100.0, 2));

        // 6) 'var' in lambda parameters (Java 11+) - useful only when you
        //    want to add annotations; shown here just so you recognise it.
        BillCalculator varStyle = (var price, var qty) -> price * qty;
        System.out.println("var style     = " + varStyle.total(50.0, 4));

        // 7) A lambda is a VALUE: we can store it, pass it, return it.
        printFinalPrice(1200, tenPercentOff);
        printFinalPrice(1200, p -> p > 1000 ? p - 200 : p);   // passed inline
    }

    // A method that RECEIVES behaviour as a parameter.
    // The caller decides HOW the discount is calculated.
    static void printFinalPrice(double price, DiscountRule rule) {
        System.out.println("Price " + price + " -> final " + rule.apply(price));
    }
}

/* Output:
Welcome to the Online Shop!
10% off 1000  = 900.0
Flat 50 off   = 950.0
Festival 25%  = 750.0
3 x 199.0     = 597.0
With 18% tax  = 236.0
var style     = 200.0
Price 1200.0 -> final 1080.0
Price 1200.0 -> final 1000.0
*/
