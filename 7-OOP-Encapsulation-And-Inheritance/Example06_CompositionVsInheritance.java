/*
 * Example06_CompositionVsInheritance.java
 * ------------------------------------------------------------
 * What this file shows:
 *   1. IS-A  (inheritance): a Truck IS-A Vehicle  -> 'extends'
 *      HAS-A (composition): a SportsCar HAS-A Engine and HAS-A MusicSystem -> a FIELD
 *   2. Composition lets you build an object from smaller parts and REPLACE a part
 *      (swap the engine) without changing the class.
 *   3. Delegation: SportsCar.start() asks its Engine to do the real work.
 *   4. A BAD use of inheritance (Car extends Engine) and why it is wrong.
 *   => "Favour composition over inheritance."
 *
 * How to run (from this folder):
 *   java Example06_CompositionVsInheritance.java
 */
public class Example06_CompositionVsInheritance {

    public static void main(String[] args) {
        // ---------- HAS-A ----------
        Engine petrol = new Engine("Petrol V6", 300);
        MusicSystem jbl = new MusicSystem("JBL");
        SportsCar car = new SportsCar("Mustang", petrol, jbl);

        car.start();
        car.playMusic("Believer");
        System.out.println(car.getModel() + " has " + car.getHorsePower() + " HP");

        // Swap a part at runtime - impossible if SportsCar EXTENDED a specific engine class.
        System.out.println();
        System.out.println("Upgrading the engine...");
        car.replaceEngine(new Engine("Electric Dual Motor", 480));
        car.start();
        System.out.println(car.getModel() + " has " + car.getHorsePower() + " HP");

        // ---------- IS-A ----------
        System.out.println();
        Truck truck = new Truck("Ashok Leyland", 16);
        truck.honk();            // inherited from MotorVehicle
        truck.loadCargo(10);     // Truck's own method
    }
}

// ===== Parts used by composition =====

class Engine {
    private final String type;
    private final int horsePower;

    Engine(String type, int horsePower) {
        this.type = type;
        this.horsePower = horsePower;
    }

    void ignite() {
        System.out.println("  Engine (" + type + ") is running");
    }

    int getHorsePower() {
        return horsePower;
    }
}

class MusicSystem {
    private final String brand;

    MusicSystem(String brand) {
        this.brand = brand;
    }

    void play(String song) {
        System.out.println("  " + brand + " speakers playing: " + song);
    }
}

/** SportsCar HAS-A Engine and HAS-A MusicSystem (composition). */
class SportsCar {
    private final String model;
    private Engine engine;                    // a part - can be swapped
    private final MusicSystem musicSystem;    // another part

    SportsCar(String model, Engine engine, MusicSystem musicSystem) {
        this.model = model;
        this.engine = engine;                 // the parts are GIVEN to the car (see the note in the lesson:
        this.musicSystem = musicSystem;       // this idea becomes "dependency injection" in Spring!)
    }

    void start() {
        System.out.println(model + " starting:");
        engine.ignite();                      // DELEGATION: the engine does the actual work
    }

    void playMusic(String song) {
        musicSystem.play(song);
    }

    void replaceEngine(Engine newEngine) {
        this.engine = newEngine;
    }

    int getHorsePower() {
        return engine.getHorsePower();
    }

    String getModel() {
        return model;
    }
}

// ===== A correct IS-A relationship =====

class MotorVehicle {
    protected final String maker;

    MotorVehicle(String maker) {
        this.maker = maker;
    }

    void honk() {
        System.out.println(maker + ": Beep beep!");
    }
}

/** Truck IS-A MotorVehicle - passes the "IS-A" test, so inheritance is fine. */
class Truck extends MotorVehicle {
    private final int capacityTons;

    Truck(String maker, int capacityTons) {
        super(maker);
        this.capacityTons = capacityTons;
    }

    void loadCargo(int tons) {
        System.out.println(maker + " truck loaded " + tons + " of " + capacityTons + " tons");
    }
}

// ===== A WRONG use of inheritance (for discussion only) =====
// class Car extends Engine { ... }
// "A Car IS-A Engine"? No! A car HAS an engine. Inheriting would expose engine methods
// directly on Car, tie Car to ONE engine type forever, and break when Engine changes.

/*
Output:
Mustang starting:
  Engine (Petrol V6) is running
  JBL speakers playing: Believer
Mustang has 300 HP

Upgrading the engine...
Mustang starting:
  Engine (Electric Dual Motor) is running
Mustang has 480 HP

Ashok Leyland: Beep beep!
Ashok Leyland truck loaded 10 of 16 tons
*/
