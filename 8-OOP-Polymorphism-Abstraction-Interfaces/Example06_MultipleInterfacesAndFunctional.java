/*
 * Example06_MultipleInterfacesAndFunctional.java
 * ------------------------------------------------------------
 * What this file shows:
 *   1. A class can IMPLEMENT MANY interfaces (SmartPhone is a Camera, a MusicPlayer and a Phone)
 *      - this is how Java gives you "multiple inheritance of type" safely.
 *   2. A class can extend ONE class AND implement interfaces at the same time.
 *   3. If two interfaces have the SAME default method, the class MUST override it and can
 *      pick a version with  InterfaceName.super.method().
 *   4. An interface can EXTEND another interface.
 *   5. Functional interface preview: an interface with exactly ONE abstract method can be
 *      implemented with a short LAMBDA expression (full details in Topic 13).
 *
 * How to run (from this folder):
 *   java Example06_MultipleInterfacesAndFunctional.java
 */
public class Example06_MultipleInterfacesAndFunctional {

    public static void main(String[] args) {

        // ---------- 1-3. Multiple interfaces ----------
        SmartPhone phone = new SmartPhone("Pixel");
        phone.call("98480-00000");
        phone.takePhoto();
        phone.playSong("Kesariya");
        phone.powerOn();             // conflicting default methods -> SmartPhone decided
        phone.charge();              // from the parent CLASS Gadget

        // The same object can be seen through ANY of its interface types:
        Camera camera = phone;
        MusicPlayer player = phone;
        camera.takePhoto();
        player.playSong("Naatu Naatu");
        System.out.println("phone instanceof Camera? " + (phone instanceof Camera));

        // ---------- 4. Interface extending an interface ----------
        System.out.println("phone instanceof Device? " + (phone instanceof Device) + " (Camera extends Device)");

        // ---------- 5. Functional interface + lambda (preview) ----------
        System.out.println();

        // The long way: a full class that implements the interface.
        DiscountRule festival = new FestivalDiscount();

        // The short way: a lambda.   (input) -> result
        DiscountRule noDiscount = price -> price;
        DiscountRule flat100 = price -> price - 100;
        DiscountRule tenPercent = price -> price * 0.9;

        double price = 2000;
        System.out.println("Festival    : " + festival.apply(price));
        System.out.println("No discount : " + noDiscount.apply(price));
        System.out.println("Flat 100 off: " + flat100.apply(price));
        System.out.println("10% off     : " + tenPercent.apply(price));

        // Behaviour passed as an argument - the method doesn't know which rule it gets.
        printFinalPrice(price, tenPercent);
        printFinalPrice(price, p -> p > 1500 ? p - 300 : p);
    }

    static void printFinalPrice(double price, DiscountRule rule) {
        System.out.println("Final price: " + rule.apply(price));
    }
}

// ===== interfaces =====

interface Device {
    default void powerOn() {
        System.out.println("Device powering on");
    }
}

interface Camera extends Device {          // an interface can extend another interface
    void takePhoto();

    @Override
    default void powerOn() {
        System.out.println("Camera lens opening");
    }
}

interface MusicPlayer {
    void playSong(String song);

    default void powerOn() {
        System.out.println("Music player warming up speakers");
    }
}

interface Phone {
    void call(String number);
}

// ===== a normal parent class =====

class Gadget {
    protected final String name;

    Gadget(String name) {
        this.name = name;
    }

    void charge() {
        System.out.println(name + " is charging");
    }
}

/** extends ONE class, implements THREE interfaces. */
class SmartPhone extends Gadget implements Camera, MusicPlayer, Phone {

    SmartPhone(String name) {
        super(name);
    }

    @Override
    public void takePhoto() {
        System.out.println(name + " clicked a photo");
    }

    @Override
    public void playSong(String song) {
        System.out.println(name + " playing " + song);
    }

    @Override
    public void call(String number) {
        System.out.println(name + " calling " + number);
    }

    // Camera AND MusicPlayer both have a default powerOn() -> ambiguity.
    // The compiler FORCES us to override it. We may call either (or both) parent versions:
    @Override
    public void powerOn() {
        Camera.super.powerOn();
        MusicPlayer.super.powerOn();
        System.out.println(name + " is ready");
    }
}

// ===== functional interface =====

/** Exactly ONE abstract method -> a functional interface -> can be written as a lambda. */
@FunctionalInterface
interface DiscountRule {
    double apply(double price);
}

class FestivalDiscount implements DiscountRule {
    @Override
    public double apply(double price) {
        return price * 0.8;     // 20% off
    }
}

/*
Output:
Pixel calling 98480-00000
Pixel clicked a photo
Pixel playing Kesariya
Camera lens opening
Music player warming up speakers
Pixel is ready
Pixel is charging
Pixel clicked a photo
Pixel playing Naatu Naatu
phone instanceof Camera? true
phone instanceof Device? true (Camera extends Device)

Festival    : 1600.0
No discount : 2000.0
Flat 100 off: 1900.0
10% off     : 1800.0
Final price: 1800.0
Final price: 1700.0
*/
