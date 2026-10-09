/*
 * Example02_Records.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   Records (Java 16): a one-line way to create an immutable "data carrier"
 *   class. Java auto-generates: private final fields, a constructor,
 *   accessor methods (name(), not getName()), equals(), hashCode(), toString().
 *
 *   - basic record
 *   - compact constructor for VALIDATION
 *   - extra methods, static factory methods, static fields
 *   - records implementing interfaces
 *   - "with" style copy (records are immutable)
 *   - records as DTOs (exactly how you'll use them in Spring REST APIs)
 *
 * HOW TO RUN:
 *   java Example02_Records.java
 */
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Example02_Records {

    // ---------- 1. The simplest record: ONE line ----------
    // Equivalent to ~50 lines of a classic class with fields, constructor,
    // getters, equals, hashCode and toString!
    record Point(int x, int y) { }

    // ---------- 2. Compact constructor = validation ----------
    record Product(String name, double price, int stock) {
        // "Compact" because you don't repeat the parameters or assign fields:
        // Java assigns them automatically AFTER this block runs.
        Product {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Product name is required");
            }
            if (price < 0) {
                throw new IllegalArgumentException("Price cannot be negative: " + price);
            }
            name = name.strip();   // you may normalise parameters before they are stored
        }

        // ---------- 3. Extra instance methods are allowed ----------
        boolean inStock() {
            return stock > 0;
        }

        double priceWithGst() {
            return price * 1.18;
        }

        // "Wither": records are immutable, so we return a NEW copy with one change
        Product withPrice(double newPrice) {
            return new Product(name, newPrice, stock);
        }

        // ---------- Static field + static factory method ----------
        static final int LOW_STOCK_LIMIT = 5;

        static Product outOfStock(String name, double price) {
            return new Product(name, price, 0);
        }
    }

    // ---------- 4. Records can implement interfaces ----------
    interface Shape {
        double area();
    }

    record Circle(double radius) implements Shape {
        public double area() {
            return Math.round(Math.PI * radius * radius * 100) / 100.0;
        }
    }

    record Rectangle(double width, double height) implements Shape {
        public double area() {
            return width * height;
        }
    }

    // ---------- 5. Records as DTOs (Data Transfer Objects) ----------
    // In Spring, a REST controller receives/returns DTOs as JSON.
    // Jackson (the JSON library) understands records out of the box.
    record CreateStudentRequest(String name, String email, int age) { }
    record StudentResponse(long id, String name, String email) { }

    public static void main(String[] args) {

        // 1. Basic usage
        Point p1 = new Point(3, 4);
        Point p2 = new Point(3, 4);
        System.out.println("1) toString     : " + p1);
        System.out.println("1) accessor x() : " + p1.x() + "  (not getX()!)");
        System.out.println("1) p1.equals(p2): " + p1.equals(p2) + "  (compares VALUES, not memory)");
        System.out.println("1) p1 == p2     : " + (p1 == p2) + " (different objects)");
        System.out.println("1) same hashCode: " + (p1.hashCode() == p2.hashCode()));

        // Because equals/hashCode are generated, records work perfectly in Sets/Map keys
        Set<Point> visited = new HashSet<>(List.of(p1, p2, new Point(0, 0)));
        System.out.println("1) set size     : " + visited.size() + " (duplicate point removed)");

        // 2. Validation in the compact constructor
        Product laptop = new Product("  Laptop  ", 55000, 3);
        System.out.println("2) product      : " + laptop + " (name was stripped)");
        try {
            new Product("Phone", -10, 1);
        } catch (IllegalArgumentException e) {
            System.out.println("2) invalid      : " + e.getMessage());
        }

        // 3. Extra methods, wither, static factory
        System.out.println("3) in stock?    : " + laptop.inStock());
        System.out.printf("3) with GST     : %.2f%n", laptop.priceWithGst());
        Product discounted = laptop.withPrice(49999);
        System.out.println("3) withPrice    : " + discounted + "  original unchanged: " + laptop.price());
        System.out.println("3) factory      : " + Product.outOfStock("Tablet", 20000));
        System.out.println("3) low stock?   : " + (laptop.stock() <= Product.LOW_STOCK_LIMIT));

        // 4. Interface + polymorphism
        List<Shape> shapes = List.of(new Circle(1), new Rectangle(2, 3));
        for (Shape s : shapes) {
            System.out.println("4) " + s + " area=" + s.area());
        }

        // 5. DTO mapping: request DTO -> (save) -> response DTO
        CreateStudentRequest request = new CreateStudentRequest("Ravi", "ravi@college.edu", 20);
        long generatedId = 101;   // pretend the database generated this
        StudentResponse response = new StudentResponse(generatedId, request.name(), request.email());
        System.out.println("5) request DTO  : " + request);
        System.out.println("5) response DTO : " + response);
    }
}

/* Output:
1) toString     : Point[x=3, y=4]
1) accessor x() : 3  (not getX()!)
1) p1.equals(p2): true  (compares VALUES, not memory)
1) p1 == p2     : false (different objects)
1) same hashCode: true
1) set size     : 2 (duplicate point removed)
2) product      : Product[name=Laptop, price=55000.0, stock=3] (name was stripped)
2) invalid      : Price cannot be negative: -10.0
3) in stock?    : true
3) with GST     : 64900.00
3) withPrice    : Product[name=Laptop, price=49999.0, stock=3]  original unchanged: 55000.0
3) factory      : Product[name=Tablet, price=20000.0, stock=0]
3) low stock?   : true
4) Circle[radius=1.0] area=3.14
4) Rectangle[width=2.0, height=3.0] area=6.0
5) request DTO  : CreateStudentRequest[name=Ravi, email=ravi@college.edu, age=20]
5) response DTO : StudentResponse[id=101, name=Ravi, email=ravi@college.edu]
*/
