/*
 * Example05_Interfaces.java
 * ------------------------------------------------------------
 * What this file shows (domain: Online payments - Credit Card, UPI, PayPal):
 *   An INTERFACE is a CONTRACT: "any class that implements me promises to have these methods".
 *   1. Abstract methods (no body)           -> every implementation MUST provide them.
 *   2. Constants (public static final)      -> MIN_AMOUNT.
 *   3. default methods (Java 8+)            -> a ready-made body; implementations MAY override.
 *   4. static methods  (Java 8+)            -> helpers called on the interface name.
 *   5. private methods (Java 9+)            -> internal helpers shared by default methods.
 *   6. 'implements' and polymorphism through an interface type.
 *
 * How to run (from this folder):
 *   java Example05_Interfaces.java
 */
public class Example05_Interfaces {

    public static void main(String[] args) {
        Payment[] payments = {
                new CreditCardPayment("4111111111111111"),
                new UpiPayment("neha@okbank"),
                new PayPalPayment("neha@example.com")
        };

        for (Payment payment : payments) {
            System.out.println("--- " + payment.methodName() + " ---");
            payment.pay(1500);                 // abstract method -> each class's own version
            payment.printReceipt(1500);        // default method (PayPal overrides it)
            System.out.println("Fee: " + payment.fee(1500));
        }

        System.out.println();
        System.out.println("Minimum amount: " + Payment.MIN_AMOUNT);           // interface constant
        System.out.println("Is 0.5 valid? " + Payment.isValidAmount(0.5));     // static method
        System.out.println("Is 250 valid? " + Payment.isValidAmount(250));
    }
}

interface Payment {

    // A field in an interface is automatically public static final (a constant).
    double MIN_AMOUNT = 1.0;

    // Abstract methods: automatically public and abstract. No body.
    boolean pay(double amount);

    String methodName();

    // default method: a body that every implementation gets for free (can be overridden).
    default void printReceipt(double amount) {
        System.out.println(line() + "\nReceipt: Rs. " + amount + " paid via " + methodName() + "\n" + line());
    }

    // Another default method with a sensible default value.
    default double fee(double amount) {
        return 0;
    }

    // static method: belongs to the interface itself. Call it as Payment.isValidAmount(...)
    static boolean isValidAmount(double amount) {
        return amount >= MIN_AMOUNT;
    }

    // private method (Java 9+): a helper ONLY for the default methods above.
    private String line() {
        return "-".repeat(30);
    }
}

class CreditCardPayment implements Payment {
    private final String cardNumber;

    CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public boolean pay(double amount) {       // must be public (interface methods are public)
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        System.out.println("Charging Rs. " + amount + " to card ending " + last4);
        return true;
    }

    @Override
    public String methodName() {
        return "Credit Card";
    }

    @Override
    public double fee(double amount) {        // cards charge a 2% fee -> override the default
        return amount * 0.02;
    }
}

class UpiPayment implements Payment {
    private final String upiId;

    UpiPayment(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("Sending UPI collect request of Rs. " + amount + " to " + upiId);
        return true;
    }

    @Override
    public String methodName() {
        return "UPI";
    }
    // fee() not overridden -> default 0 is used. UPI is free!
}

class PayPalPayment implements Payment {
    private final String email;

    PayPalPayment(String email) {
        this.email = email;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("Redirecting " + email + " to PayPal to pay Rs. " + amount);
        return true;
    }

    @Override
    public String methodName() {
        return "PayPal";
    }

    @Override
    public void printReceipt(double amount) {  // overriding a DEFAULT method is allowed
        System.out.println("PayPal will email the receipt to " + email);
    }

    @Override
    public double fee(double amount) {
        return 10 + amount * 0.03;              // fixed fee + 3%
    }
}

/*
Output:
--- Credit Card ---
Charging Rs. 1500.0 to card ending 1111
------------------------------
Receipt: Rs. 1500.0 paid via Credit Card
------------------------------
Fee: 30.0
--- UPI ---
Sending UPI collect request of Rs. 1500.0 to neha@okbank
------------------------------
Receipt: Rs. 1500.0 paid via UPI
------------------------------
Fee: 0.0
--- PayPal ---
Redirecting neha@example.com to PayPal to pay Rs. 1500.0
PayPal will email the receipt to neha@example.com
Fee: 55.0

Minimum amount: 1.0
Is 0.5 valid? false
Is 250 valid? true
*/
