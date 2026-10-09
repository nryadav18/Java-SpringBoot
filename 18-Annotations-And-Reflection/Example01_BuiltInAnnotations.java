import java.util.ArrayList;
import java.util.List;

/*
 * Example01_BuiltInAnnotations.java
 * ------------------------------------------------------------
 * What it shows:
 *   The 4 built-in annotations every Java developer uses:
 *     @Override             - "I am overriding a parent method. Compiler, please check!"
 *     @Deprecated           - "Don't use this any more, there is something better."
 *     @FunctionalInterface  - "This interface must have exactly ONE abstract method."
 *     @SuppressWarnings     - "Compiler, I know about this warning, hide it here."
 *
 *   Annotations are LABELS (metadata) on code. By themselves they do nothing;
 *   some tool (the compiler, a framework like Spring, a test runner) reads them
 *   and acts on them.
 *
 * How to run:
 *   java Example01_BuiltInAnnotations.java
 */
public class Example01_BuiltInAnnotations {

    // ---------- @Override ----------
    static class Animal {
        String sound() {
            return "...";
        }
    }

    static class Dog extends Animal {
        @Override                 // compiler checks that Animal really has sound()
        String sound() {
            return "Woof";
        }

        // If you misspell the name, @Override turns a silent bug into a compile error:
        // @Override
        // String sund() { return "Woof"; }   // ERROR: method does not override or implement
        //                                     //        a method from a supertype
    }

    // ---------- @Deprecated ----------
    static class PriceCalculator {
        /**
         * @deprecated use {@link #priceWithTax(double, double)} - the tax rate is not always 18%.
         */
        @Deprecated(since = "2.0", forRemoval = true)
        static double priceWithGst(double price) {
            return price * 1.18;
        }

        static double priceWithTax(double price, double taxRate) {
            return price * (1 + taxRate);
        }
    }

    // ---------- @FunctionalInterface ----------
    @FunctionalInterface          // compiler error if someone adds a 2nd abstract method
    interface DiscountRule {
        double apply(double price);
        // double other(double x);   // uncomment -> compile error: not a functional interface
    }

    // ---------- @SuppressWarnings ----------
    @SuppressWarnings({"rawtypes", "unchecked"})  // hide the warnings caused by the raw List below
    static List oldStyleList() {
        List list = new ArrayList();               // raw type (pre-generics style)
        list.add("legacy");
        return list;
    }

    // NOTE: javac warns about deprecated methods only when ANOTHER top-level class uses them.
    // In another class you would silence that warning with @SuppressWarnings("removal").
    public static void main(String[] args) {
        Animal pet = new Dog();
        System.out.println("Dog says: " + pet.sound());

        // Calling a deprecated method still works, but the compiler warns you
        // (and VS Code shows it with a strike-through line).
        System.out.println("Old GST price: " + PriceCalculator.priceWithGst(100));
        System.out.println("New tax price: " + PriceCalculator.priceWithTax(100, 0.05));

        DiscountRule tenPercentOff = price -> price * 0.9;  // a lambda fits a functional interface
        System.out.println("After discount: " + tenPercentOff.apply(500));

        System.out.println("Legacy list: " + oldStyleList());
    }
}

/*
 * Output:
 * Dog says: Woof
 * Old GST price: 118.0
 * New tax price: 105.0
 * After discount: 450.0
 * Legacy list: [legacy]
 */
