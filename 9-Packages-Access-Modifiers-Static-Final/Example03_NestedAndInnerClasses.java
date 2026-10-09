/*
 * Example03_NestedAndInnerClasses.java
 *
 * What this file demonstrates - classes declared INSIDE other code:
 *   1. static nested class  -> like a normal class, just "grouped" inside another class.
 *                              Does NOT need an outer object. (Very common: Builders, Map.Entry)
 *   2. inner class           -> a NON-static nested class. Each inner object is tied to an
 *                              outer object and can read the outer object's fields.
 *   3. local class           -> a class declared inside a method (rare).
 *   4. anonymous class       -> a class with NO name, created and used in one place.
 *                              Used for one-off implementations of an interface.
 *                              (In Topic 13 you will replace many of these with lambdas.)
 *
 * Domain: a food-delivery order.
 *
 * How to run:
 *   java Example03_NestedAndInnerClasses.java
 *
 * Output:
 *   --- 1. static nested class ---
 *   Address: 12 MG Road, Bengaluru - 560001
 *   --- 2. inner class ---
 *   Order #501 item: Masala Dosa x2 = Rs.180.0
 *   Order #501 item: Filter Coffee x1 = Rs.40.0
 *   Order #501 total = Rs.220.0
 *   --- 3. local class ---
 *   Bill line: Delivery fee -> Rs.30.0
 *   --- 4. anonymous class ---
 *   Festival offer: 220.0 -> 198.0
 *   No offer: 220.0 -> 220.0
 */
public class Example03_NestedAndInnerClasses {

    public static void main(String[] args) {
        System.out.println("--- 1. static nested class ---");
        // Created WITHOUT any FoodOrder object: OuterClass.NestedClass
        FoodOrder.Address home = new FoodOrder.Address("12 MG Road", "Bengaluru", "560001");
        System.out.println("Address: " + home);

        System.out.println("--- 2. inner class ---");
        FoodOrder order = new FoodOrder(501, home);
        // Inner objects need an outer object. Normally the outer class creates them (addItem below).
        order.addItem("Masala Dosa", 2, 90.0);
        order.addItem("Filter Coffee", 1, 40.0);
        order.printItems();
        System.out.println("Order #501 total = Rs." + order.total());
        // The rare direct syntax would be:  FoodOrder.Item i = order.new Item("Idli", 1, 30.0);

        System.out.println("--- 3. local class ---");
        printDeliveryFee();

        System.out.println("--- 4. anonymous class ---");
        // We need a Discount just ONCE -> no need to write a named class for it.
        Discount festivalOffer = new Discount() {          // "new Interface() { ...body... }"
            @Override
            public double apply(double amount) {
                return amount - amount * 0.10;             // 10% off
            }
        };
        Discount noOffer = new Discount() {
            @Override
            public double apply(double amount) {
                return amount;
            }
        };
        System.out.println("Festival offer: " + order.total() + " -> " + festivalOffer.apply(order.total()));
        System.out.println("No offer: " + order.total() + " -> " + noOffer.apply(order.total()));
    }

    static void printDeliveryFee() {
        // A LOCAL class: only visible inside this method.
        class BillLine {
            final String label;
            final double amount;
            BillLine(String label, double amount) { this.label = label; this.amount = amount; }
            String show() { return label + " -> Rs." + amount; }
        }
        BillLine fee = new BillLine("Delivery fee", 30.0);
        System.out.println("Bill line: " + fee.show());
    }
}

// An interface with ONE method - perfect for anonymous classes (and later, lambdas).
interface Discount {
    double apply(double amount);
}

class FoodOrder {

    private final int orderId;
    private final Address deliveryAddress;
    private final Item[] items = new Item[10];   // simple array; collections come in Topic 11
    private int itemCount = 0;

    FoodOrder(int orderId, Address deliveryAddress) {
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
    }

    void addItem(String name, int qty, double price) {
        items[itemCount++] = new Item(name, qty, price);   // creates an INNER object tied to "this" order
    }

    void printItems() {
        for (int i = 0; i < itemCount; i++) {
            items[i].print();
        }
    }

    double total() {
        double sum = 0;
        for (int i = 0; i < itemCount; i++) {
            sum += items[i].lineTotal();
        }
        return sum;
    }

    // ---------- 1. STATIC NESTED CLASS ----------
    // An address makes sense on its own; it does not need a specific order.
    // So it is "static": no hidden link to a FoodOrder object.
    static class Address {
        private final String street;
        private final String city;
        private final String pin;

        Address(String street, String city, String pin) {
            this.street = street;
            this.city = city;
            this.pin = pin;
        }

        @Override
        public String toString() {
            return street + ", " + city + " - " + pin;
        }
    }

    // ---------- 2. INNER CLASS (non-static) ----------
    // An item belongs to ONE specific order and wants to read that order's orderId.
    class Item {
        private final String name;
        private final int qty;
        private final double price;

        Item(String name, int qty, double price) {
            this.name = name;
            this.qty = qty;
            this.price = price;
        }

        double lineTotal() {
            return qty * price;
        }

        void print() {
            // "orderId" belongs to the OUTER object. An inner class can read it directly.
            // (The full form is FoodOrder.this.orderId.)
            System.out.println("Order #" + orderId + " item: " + name + " x" + qty + " = Rs." + lineTotal());
        }
    }
}
