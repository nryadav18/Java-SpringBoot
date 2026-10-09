/*
 * Example03_SuperAndConstructorOrder.java
 * ------------------------------------------------------------
 * What this file shows (domain: Vehicles), a 3-level chain:  Vehicle -> Car -> ElectricCar
 *   1. Constructor call ORDER: parent constructors run FIRST (top to bottom).
 *   2. super(...)        -> call the parent's constructor (must be the first line).
 *   3. If you don't write super(...), Java inserts super() (no arguments) for you.
 *   4. super.method()    -> call the parent's version of a method.
 *   5. super.field vs this.field when names clash (shown for learning; avoid in real code).
 *
 * How to run (from this folder):
 *   java Example03_SuperAndConstructorOrder.java
 */
public class Example03_SuperAndConstructorOrder {

    public static void main(String[] args) {
        System.out.println("Creating an ElectricCar...");
        ElectricCar tesla = new ElectricCar("Tesla", 5, 75);

        System.out.println();
        tesla.describe();

        System.out.println();
        tesla.showNames();

        System.out.println();
        System.out.println("Creating a Bicycle (no explicit super call)...");
        new Bicycle();
    }
}

class Vehicle {
    protected String brand;               // protected: visible to subclasses (see Example05)
    protected String type = "Vehicle";

    Vehicle() {                           // no-arg constructor - used by the implicit super()
        System.out.println("1. Vehicle() constructor");
    }

    Vehicle(String brand) {
        this.brand = brand;
        System.out.println("1. Vehicle(String) constructor -> brand = " + brand);
    }

    void describe() {
        System.out.println("Brand: " + brand);
    }
}

class Car extends Vehicle {
    protected int seats;
    protected String type = "Car";        // SAME name as the parent's field (this hides it)

    Car(String brand, int seats) {
        super(brand);                     // explicitly choose Vehicle(String)
        this.seats = seats;
        System.out.println("2. Car constructor -> seats = " + seats);
    }

    @Override
    void describe() {
        super.describe();                 // first do what Vehicle does ...
        System.out.println("Seats: " + seats);   // ... then add Car details
    }
}

class ElectricCar extends Car {
    private int batteryKwh;

    ElectricCar(String brand, int seats, int batteryKwh) {
        super(brand, seats);              // Car's constructor (which calls Vehicle's)
        this.batteryKwh = batteryKwh;
        System.out.println("3. ElectricCar constructor -> battery = " + batteryKwh + " kWh");
    }

    @Override
    void describe() {
        super.describe();                 // Car.describe() -> which calls Vehicle.describe()
        System.out.println("Battery: " + batteryKwh + " kWh");
    }

    void showNames() {
        System.out.println("type       = " + type);         // nearest one: Car's field
        System.out.println("super.type = " + super.type);   // the parent (Car) version
        System.out.println("((Vehicle) this).type = " + ((Vehicle) this).type); // grand-parent's field
    }
}

/** Implicit super(): this class never calls super(...), so Java calls Vehicle() for it. */
class Bicycle extends Vehicle {
    Bicycle() {
        // Java secretly inserts:  super();
        System.out.println("2. Bicycle constructor");
    }
}

/*
Output:
Creating an ElectricCar...
1. Vehicle(String) constructor -> brand = Tesla
2. Car constructor -> seats = 5
3. ElectricCar constructor -> battery = 75 kWh

Brand: Tesla
Seats: 5
Battery: 75 kWh

type       = Car
super.type = Car
((Vehicle) this).type = Vehicle

Creating a Bicycle (no explicit super call)...
1. Vehicle() constructor
2. Bicycle constructor
*/
