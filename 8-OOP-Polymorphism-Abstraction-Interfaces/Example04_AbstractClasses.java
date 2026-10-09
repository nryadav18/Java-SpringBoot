/*
 * Example04_AbstractClasses.java
 * ------------------------------------------------------------
 * What this file shows (domain: Shapes and their area):
 *   ABSTRACTION = show WHAT an object can do, hide HOW it does it.
 *   1. An ABSTRACT class: 'abstract class Shape' - you cannot create 'new Shape()'.
 *   2. ABSTRACT methods: declared without a body; every concrete child MUST implement them.
 *   3. An abstract class can ALSO have fields, a constructor and normal (concrete) methods.
 *   4. A concrete method can call abstract methods (the "template" idea).
 *   5. Polymorphism: a Shape[] array holding circles, rectangles and triangles.
 *
 * How to run (from this folder):
 *   java Example04_AbstractClasses.java
 */
public class Example04_AbstractClasses {

    public static void main(String[] args) {
        // Shape s = new Shape("mystery");   // COMPILE ERROR: Shape is abstract; cannot be instantiated

        Shape[] shapes = {
                new Circle(7),
                new Rectangle(4, 5),
                new Triangle(3, 4, 5)
        };

        double totalArea = 0;
        for (Shape shape : shapes) {
            shape.printReport();             // concrete method from Shape, uses abstract ones
            totalArea += shape.area();       // each shape calculates its OWN area
        }
        System.out.printf("Total area of all shapes: %.2f%n", totalArea);

        // Find the biggest shape - works for ANY shape, even ones added later.
        Shape biggest = shapes[0];
        for (Shape shape : shapes) {
            if (shape.area() > biggest.area()) {
                biggest = shape;
            }
        }
        System.out.println("Biggest shape: " + biggest.getName());
    }
}

abstract class Shape {
    private final String name;            // abstract classes CAN have fields ...

    protected Shape(String name) {        // ... and constructors (called by children via super)
        this.name = name;
    }

    // ABSTRACT methods: no body, just a promise. "Every shape has an area - but I can't
    // know how to calculate it until I know which shape it is."
    abstract double area();

    abstract double perimeter();

    // CONCRETE method: shared by all shapes. It calls the abstract methods; at runtime
    // the child's implementation is used.
    void printReport() {
        System.out.printf("%-9s area = %7.2f, perimeter = %6.2f%n", name, area(), perimeter());
    }

    String getName() {
        return name;
    }
}

class Circle extends Shape {
    private final double radius;

    Circle(double radius) {
        super("Circle");
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * radius * radius;
    }

    @Override
    double perimeter() {
        return 2 * Math.PI * radius;
    }
}

class Rectangle extends Shape {
    private final double width;
    private final double height;

    Rectangle(double width, double height) {
        super("Rectangle");
        this.width = width;
        this.height = height;
    }

    @Override
    double area() {
        return width * height;
    }

    @Override
    double perimeter() {
        return 2 * (width + height);
    }
}

class Triangle extends Shape {
    private final double a, b, c;    // the three sides

    Triangle(double a, double b, double c) {
        super("Triangle");
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    double area() {
        double s = perimeter() / 2;                          // Heron's formula
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    @Override
    double perimeter() {
        return a + b + c;
    }
}

// If a child does NOT implement all abstract methods, it must be abstract too:
// class Square extends Shape { }   // COMPILE ERROR: Square is not abstract and does not override area()

/*
Output:
Circle    area =  153.94, perimeter =  43.98
Rectangle area =   20.00, perimeter =  18.00
Triangle  area =    6.00, perimeter =  12.00
Total area of all shapes: 179.94
Biggest shape: Circle
*/
