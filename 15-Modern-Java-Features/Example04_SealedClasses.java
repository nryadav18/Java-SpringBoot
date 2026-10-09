/*
 * Example04_SealedClasses.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   Sealed classes and interfaces (Java 17): YOU decide exactly which
 *   classes are allowed to extend/implement a type.
 *
 *     sealed interface Payment permits CardPayment, UpiPayment, WalletPayment
 *
 *   Every permitted subtype must be one of:
 *     final      - nobody can extend it further
 *     sealed     - it again lists its own permitted subtypes
 *     non-sealed - open again: anybody may extend it
 *   (records and enums are automatically final)
 *
 *   Combined with records + pattern matching for instanceof, this models
 *   "one of a fixed set of shapes" very safely.
 *
 * HOW TO RUN:
 *   java Example04_SealedClasses.java
 */
import java.util.List;

public class Example04_SealedClasses {

    // ---------- A sealed interface with three permitted implementations ----------
    sealed interface Payment permits CardPayment, UpiPayment, WalletPayment { }

    // records are implicitly final -> allowed as permitted subtypes
    record CardPayment(String cardNumber, double amount) implements Payment { }

    record UpiPayment(String upiId, double amount) implements Payment { }

    // non-sealed: WalletPayment is open again, so specific wallets can extend it
    static non-sealed class WalletPayment implements Payment {
        private final String walletName;
        private final double amount;

        WalletPayment(String walletName, double amount) {
            this.walletName = walletName;
            this.amount = amount;
        }

        String walletName() { return walletName; }
        double amount() { return amount; }
    }

    static class PaytmWallet extends WalletPayment {   // allowed because WalletPayment is non-sealed
        PaytmWallet(double amount) {
            super("Paytm", amount);
        }
    }

    // This would NOT compile - CashPayment is not in the 'permits' list:
    // record CashPayment(double amount) implements Payment { }

    // ---------- A sealed abstract class hierarchy ----------
    static sealed abstract class Vehicle permits Car, Truck {
        abstract int wheels();
    }

    static final class Car extends Vehicle {
        int wheels() { return 4; }
    }

    static final class Truck extends Vehicle {
        int wheels() { return 6; }
    }

    // Processing: because the list of subtypes is CLOSED, we know these are
    // ALL the cases. Pattern matching for instanceof (Java 16) gives us a
    // ready-cast variable (card, upi, wallet) - no manual casting.
    static String process(Payment payment) {
        if (payment instanceof CardPayment card) {
            String last4 = card.cardNumber().substring(card.cardNumber().length() - 4);
            return "Charging card ****" + last4 + " Rs." + card.amount();
        } else if (payment instanceof UpiPayment upi) {
            return "Sending UPI request to " + upi.upiId() + " Rs." + upi.amount();
        } else if (payment instanceof WalletPayment wallet) {
            return "Debiting " + wallet.walletName() + " wallet Rs." + wallet.amount();
        }
        throw new IllegalStateException("Unknown payment type");   // can never happen
        // In newer Java (21+) you can write: switch (payment) { case CardPayment c -> ...; }
        // and the compiler checks you covered every permitted type.
    }

    public static void main(String[] args) {
        List<Payment> payments = List.of(
                new CardPayment("4111222233334444", 2500.0),
                new UpiPayment("asha@okbank", 799.0),
                new WalletPayment("PhonePe", 150.0),
                new PaytmWallet(300.0));

        for (Payment p : payments) {
            System.out.println(process(p));
        }

        // Reflection can tell you the permitted subclasses
        System.out.println("Payment is sealed?  " + Payment.class.isSealed());
        System.out.print("Permitted types:   ");
        for (Class<?> c : Payment.class.getPermittedSubclasses()) {
            System.out.print(c.getSimpleName() + " ");
        }
        System.out.println();

        List<Vehicle> fleet = List.of(new Car(), new Truck(), new Car());
        int totalWheels = 0;
        for (Vehicle v : fleet) {
            totalWheels += v.wheels();
        }
        System.out.println("Fleet wheels:      " + totalWheels);
    }
}

/* Output:
Charging card ****4444 Rs.2500.0
Sending UPI request to asha@okbank Rs.799.0
Debiting PhonePe wallet Rs.150.0
Debiting Paytm wallet Rs.300.0
Payment is sealed?  true
Permitted types:   CardPayment UpiPayment WalletPayment
Fleet wheels:      14
*/
