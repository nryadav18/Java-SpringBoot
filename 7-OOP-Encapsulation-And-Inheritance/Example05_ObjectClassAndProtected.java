/*
 * Example05_ObjectClassAndProtected.java
 * ------------------------------------------------------------
 * What this file shows (domain: an Online Shop):
 *   1. Every class extends java.lang.Object (even if you don't write 'extends').
 *      So every object has toString(), equals(), hashCode(), getClass().
 *   2. The 'protected' access modifier: visible to subclasses (and the same package).
 *   3. Types of inheritance Java allows with classes:
 *        single      (Product -> DigitalProduct)
 *        multilevel  (Product -> DigitalProduct -> EBook)
 *        hierarchical(Product -> DigitalProduct AND Product -> PhysicalProduct)
 *      and why MULTIPLE inheritance of classes is NOT allowed.
 *
 * How to run (from this folder):
 *   java Example05_ObjectClassAndProtected.java
 */
public class Example05_ObjectClassAndProtected {

    public static void main(String[] args) {
        Product phone = new PhysicalProduct("Phone", 15000, 0.2);
        Product course = new DigitalProduct("Java Course", 499, 1200);
        EBook ebook = new EBook("Clean Code", 350, 5, 464);

        // ---------- 1. methods every object has (from Object) ----------
        Object anything = ebook;                 // ANY object fits in an Object variable
        System.out.println("toString()  : " + anything);          // overridden in Product
        System.out.println("getClass()  : " + anything.getClass().getSimpleName());
        System.out.println("hashCode()  : same for same object? " + (ebook.hashCode() == anything.hashCode()));
        System.out.println("equals(self): " + ebook.equals(anything));

        // A class that overrides NOTHING still has these methods:
        Empty empty = new Empty();
        System.out.println("Empty's superclass: " + empty.getClass().getSuperclass().getName());

        // ---------- 2. protected in action ----------
        System.out.println();
        System.out.println(phone.label() + " -> final price " + phone.finalPrice());
        System.out.println(course.label() + " -> final price " + course.finalPrice());
        System.out.println(ebook.label() + " -> final price " + ebook.finalPrice() + ", pages " + ebook.getPages());

        // ---------- 3. inheritance chain ----------
        System.out.println();
        System.out.print("EBook's family tree: ");
        Class<?> type = EBook.class;
        while (type != null) {                   // walk up the parents until there are none
            System.out.print(type.getSimpleName() + (type.getSuperclass() != null ? " -> " : "\n"));
            type = type.getSuperclass();
        }
    }
}

/** No 'extends' written, but the compiler treats it as 'class Empty extends Object'. */
class Empty {
}

class Product {
    protected String name;          // protected: subclasses may read/write it directly
    protected double price;
    private String sku;             // private: only Product itself can see it

    Product(String name, double price) {
        this.name = name;
        this.price = price;
        this.sku = "SKU-" + name.toUpperCase().replace(" ", "-");
    }

    double finalPrice() {
        return price;
    }

    String label() {
        return name + " [" + sku + "]";
    }

    @Override
    public String toString() {            // overriding Object's toString()
        return getClass().getSimpleName() + "{name='" + name + "', price=" + price + "}";
    }
}

/** Single inheritance: PhysicalProduct -> Product. Adds delivery cost. */
class PhysicalProduct extends Product {
    private final double weightKg;

    PhysicalProduct(String name, double price, double weightKg) {
        super(name, price);
        this.weightKg = weightKg;
    }

    @Override
    double finalPrice() {
        double delivery = 40 + weightKg * 10;   // using the protected field 'price' below
        return price + delivery;
        // return price + sku;  // would NOT compile: sku is private in Product
    }
}

/** Hierarchical: DigitalProduct is a second child of Product. Digital items have 18% GST. */
class DigitalProduct extends Product {
    protected int fileSizeMb;

    DigitalProduct(String name, double price, int fileSizeMb) {
        super(name, price);
        this.fileSizeMb = fileSizeMb;
    }

    @Override
    double finalPrice() {
        return Math.round(price * 1.18 * 100) / 100.0;   // add 18% tax, round to 2 decimals
    }
}

/** Multilevel: EBook -> DigitalProduct -> Product -> Object. */
class EBook extends DigitalProduct {
    private final int pages;

    EBook(String name, double price, int fileSizeMb, int pages) {
        super(name, price, fileSizeMb);
        this.pages = pages;
    }

    int getPages() {
        return pages;
    }
}

// MULTIPLE inheritance of classes is NOT allowed. This would not compile:
// class SmartWatch extends PhysicalProduct, DigitalProduct { }
// If both parents had a finalPrice() method, which one should SmartWatch use?
// (This confusion is called the "diamond problem".) Java solves it with
// interfaces instead - see Topic 8.

/*
Output:
toString()  : EBook{name='Clean Code', price=350.0}
getClass()  : EBook
hashCode()  : same for same object? true
equals(self): true
Empty's superclass: java.lang.Object

Phone [SKU-PHONE] -> final price 15042.0
Java Course [SKU-JAVA-COURSE] -> final price 588.82
Clean Code [SKU-CLEAN-CODE] -> final price 413.0, pages 464

EBook's family tree: EBook -> DigitalProduct -> Product -> Object
*/
