/*
 * Example04_Singleton.java
 *
 * What this file demonstrates - the Singleton design pattern:
 *   "Make sure a class has ONLY ONE object in the whole program, and give everyone
 *    a single global way to get it."
 *
 *   Recipe (uses everything from this topic!):
 *     1. private constructor          -> nobody outside can call "new"
 *     2. private static final field   -> holds the ONE object
 *     3. public static getInstance()  -> the only way to get that object
 *
 *   Shown here:
 *     - AppConfig      : EAGER singleton (object created when the class loads) - simplest & thread-safe
 *     - IdGenerator    : LAZY singleton (object created on first use) with "synchronized"
 *     - CurrencyFormatter : ENUM singleton (shortest, also safe) - just a preview, enums are in Topic 15
 *
 *   Spring note: in Spring, beans are singletons BY DEFAULT - but Spring manages the single object
 *   for you, so you will almost never write getInstance() yourself in a Spring app.
 *
 * How to run:
 *   java Example04_Singleton.java
 *
 * Output:
 *   App name: ShopEasy, max cart items: 20
 *   Same AppConfig object? true
 *   Order id: ORD-1
 *   Order id: ORD-2
 *   Order id: ORD-3
 *   Same IdGenerator object? true
 *   Formatted: Rs.1,499.50
 */
public class Example04_Singleton {

    public static void main(String[] args) {
        AppConfig config1 = AppConfig.getInstance();
        AppConfig config2 = AppConfig.getInstance();
        System.out.println("App name: " + config1.getAppName() + ", max cart items: " + config1.getMaxCartItems());
        // == compares REFERENCES: true means both variables point to the very same object.
        System.out.println("Same AppConfig object? " + (config1 == config2));

        // AppConfig c = new AppConfig();   // compile error: AppConfig() has private access in AppConfig

        // Different parts of the program share ONE counter -> no duplicate ids.
        System.out.println("Order id: " + IdGenerator.getInstance().nextId());
        System.out.println("Order id: " + IdGenerator.getInstance().nextId());
        System.out.println("Order id: " + IdGenerator.getInstance().nextId());
        System.out.println("Same IdGenerator object? " + (IdGenerator.getInstance() == IdGenerator.getInstance()));

        System.out.println("Formatted: " + CurrencyFormatter.INSTANCE.format(1499.5));
    }
}

// ---------- 1. EAGER singleton ----------
final class AppConfig {                       // final: no subclass can sneak in a second object

    // Created once, when the JVM loads the class. The JVM guarantees this is thread-safe.
    private static final AppConfig INSTANCE = new AppConfig();

    private final String appName;
    private final int maxCartItems;

    private AppConfig() {                     // private: "new AppConfig()" is blocked outside
        this.appName = "ShopEasy";
        this.maxCartItems = 20;
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    public String getAppName()    { return appName; }
    public int getMaxCartItems()  { return maxCartItems; }
}

// ---------- 2. LAZY singleton ----------
final class IdGenerator {

    private static IdGenerator instance;      // starts as null - not created yet
    private int counter = 0;

    private IdGenerator() { }

    // "synchronized" = only one thread at a time may run this method (details in Topic 17).
    // Without it, two threads could both see null and create TWO objects.
    public static synchronized IdGenerator getInstance() {
        if (instance == null) {
            instance = new IdGenerator();     // created only on the first call
        }
        return instance;
    }

    public synchronized String nextId() {
        counter++;
        return "ORD-" + counter;
    }
}

// ---------- 3. ENUM singleton (preview) ----------
// An enum with exactly one constant. Java itself guarantees there is only one INSTANCE.
enum CurrencyFormatter {
    INSTANCE;

    String format(double amount) {
        return String.format("Rs.%,.2f", amount);   // %,.2f -> thousands separator + 2 decimals
    }
}
