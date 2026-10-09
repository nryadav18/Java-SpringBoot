/*
 * Example01_ClassAndObject.java
 * ------------------------------------------------------------
 * What this file shows:
 *   1. A CLASS is a blueprint (Car). An OBJECT is a real thing built from it (myCar, yourCar).
 *   2. Fields   = what an object KNOWS (its data / state): brand, color, speed.
 *   3. Methods  = what an object can DO (its behaviour): accelerate(), brake(), describe().
 *   4. Each object has its OWN copy of the fields.
 *   5. Fields get default values (0, null, false) if you don't set them.
 *
 * How to run (from this folder):
 *   java Example01_ClassAndObject.java
 *
 * Note: the class with main() must come FIRST in the file for "java File.java" to work.
 */
public class Example01_ClassAndObject {

    public static void main(String[] args) {

        // 'new Car()' builds a NEW object from the Car blueprint.
        // 'myCar' is a variable that holds a REFERENCE (an address) to that object.
        Car myCar = new Car();
        myCar.brand = "Tata";        // set the fields of THIS object using the dot (.)
        myCar.color = "Red";

        Car yourCar = new Car();     // a second, completely separate object
        yourCar.brand = "Honda";
        yourCar.color = "White";

        // Calling methods: object.method()
        myCar.accelerate(30);
        myCar.accelerate(20);
        yourCar.accelerate(60);
        yourCar.brake(15);

        myCar.describe();
        yourCar.describe();

        // Each object keeps its own state - changing one does not touch the other.
        System.out.println("myCar speed = " + myCar.speed + ", yourCar speed = " + yourCar.speed);

        // Default values: we never set these fields on a fresh Car.
        Car emptyCar = new Car();
        System.out.println("Default brand: " + emptyCar.brand);       // null  (objects default to null)
        System.out.println("Default speed: " + emptyCar.speed);       // 0     (numbers default to 0)
        System.out.println("Default isElectric: " + emptyCar.isElectric); // false
    }
}

/**
 * The BLUEPRINT. It describes what every Car has and can do.
 * Writing this class creates NO car yet - just like a drawing of a car is not a car.
 */
class Car {
    // ----- fields (state) -----
    String brand;
    String color;
    int speed;            // km/h
    boolean isElectric;

    // ----- methods (behaviour) -----
    void accelerate(int amount) {
        speed = speed + amount;     // 'speed' here means THIS car's speed
        System.out.println(brand + " speeds up to " + speed + " km/h");
    }

    void brake(int amount) {
        speed = Math.max(0, speed - amount);   // speed can't go below 0
        System.out.println(brand + " slows down to " + speed + " km/h");
    }

    void describe() {
        System.out.println("This is a " + color + " " + brand + " going at " + speed + " km/h");
    }
}

/*
Output:
Tata speeds up to 30 km/h
Tata speeds up to 50 km/h
Honda speeds up to 60 km/h
Honda slows down to 45 km/h
This is a Red Tata going at 50 km/h
This is a White Honda going at 45 km/h
myCar speed = 50, yourCar speed = 45
Default brand: null
Default speed: 0
Default isElectric: false
*/
