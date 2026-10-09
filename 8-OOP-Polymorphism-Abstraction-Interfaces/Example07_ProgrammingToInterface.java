/*
 * Example07_ProgrammingToInterface.java
 * ------------------------------------------------------------
 * What this file shows - THE most important idea for Spring later:
 *   "Program to an INTERFACE, not to an implementation."
 *
 *   PART A (bad):  TightCheckoutService creates 'new RazorpayGateway()' itself.
 *                  It is GLUED to Razorpay. Switching or testing means editing its code.
 *   PART B (good): CheckoutService only knows the PaymentGateway INTERFACE and RECEIVES
 *                  an implementation through its constructor. -> LOOSE COUPLING.
 *                  - switch Razorpay -> Stripe without touching CheckoutService
 *                  - plug in a FakePaymentGateway for testing (no real money!)
 *
 *   In Spring, the framework does the "hand me an implementation" part for you.
 *   That is called Dependency Injection (Topics 22-24).
 *
 * How to run (from this folder):
 *   java Example07_ProgrammingToInterface.java
 */
public class Example07_ProgrammingToInterface {

    public static void main(String[] args) {

        System.out.println("=== PART A: tightly coupled ===");
        TightCheckoutService tight = new TightCheckoutService();
        tight.checkout("ORD-1", 799);
        // Want Stripe instead? You must EDIT TightCheckoutService. Want a test without
        // real payments? Impossible - it always creates a real RazorpayGateway.

        System.out.println();
        System.out.println("=== PART B: loosely coupled (program to an interface) ===");

        // The "assembler" (main here, Spring later) decides WHICH implementation to use.
        PaymentGateway gateway = new RazorpayGateway();       // variable type = the INTERFACE
        CheckoutService checkout = new CheckoutService(gateway);
        checkout.checkout("ORD-2", 1299);

        // Switch provider: one line changes, CheckoutService is untouched.
        CheckoutService stripeCheckout = new CheckoutService(new StripeGateway());
        stripeCheckout.checkout("ORD-3", 1299);

        // Testing: use a fake that records calls instead of charging money.
        System.out.println();
        System.out.println("=== Testing with a fake gateway ===");
        FakePaymentGateway fake = new FakePaymentGateway(false);   // simulate a declined payment
        CheckoutService underTest = new CheckoutService(fake);
        boolean ok = underTest.checkout("ORD-TEST", 500);
        System.out.println("Test result: checkout returned " + ok
                + ", gateway was called " + fake.getCallCount() + " time(s)"
                + (ok ? "" : " -> TEST PASSED (declined payment handled)"));
    }
}

// ===================== the contract =====================

interface PaymentGateway {
    boolean charge(String orderId, double amount);
}

// ===================== implementations =====================

class RazorpayGateway implements PaymentGateway {
    @Override
    public boolean charge(String orderId, double amount) {
        System.out.println("  [Razorpay] charged Rs. " + amount + " for " + orderId);
        return true;
    }
}

class StripeGateway implements PaymentGateway {
    @Override
    public boolean charge(String orderId, double amount) {
        System.out.println("  [Stripe] charged Rs. " + amount + " for " + orderId);
        return true;
    }
}

/** A test double: no network, no money, fully controllable. */
class FakePaymentGateway implements PaymentGateway {
    private final boolean succeed;
    private int callCount;

    FakePaymentGateway(boolean succeed) {
        this.succeed = succeed;
    }

    @Override
    public boolean charge(String orderId, double amount) {
        callCount++;
        System.out.println("  [Fake] pretending to charge " + amount + " -> " + (succeed ? "approved" : "declined"));
        return succeed;
    }

    int getCallCount() {
        return callCount;
    }
}

// ===================== PART A: tight coupling (avoid) =====================

class TightCheckoutService {
    // Hard-wired to ONE concrete class. 'new' inside the class = tight coupling.
    private final RazorpayGateway gateway = new RazorpayGateway();

    boolean checkout(String orderId, double amount) {
        System.out.println("Checking out " + orderId);
        return gateway.charge(orderId, amount);
    }
}

// ===================== PART B: loose coupling (prefer) =====================

class CheckoutService {
    // Depends only on the INTERFACE. It has no idea which company processes the payment.
    private final PaymentGateway gateway;

    // The dependency is GIVEN to us (constructor injection). We never call 'new' for it.
    CheckoutService(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    boolean checkout(String orderId, double amount) {
        System.out.println("Checking out " + orderId);
        boolean paid = gateway.charge(orderId, amount);
        System.out.println(paid ? "  Order " + orderId + " confirmed" : "  Payment failed for " + orderId);
        return paid;
    }
}

/*
Output:
=== PART A: tightly coupled ===
Checking out ORD-1
  [Razorpay] charged Rs. 799.0 for ORD-1

=== PART B: loosely coupled (program to an interface) ===
Checking out ORD-2
  [Razorpay] charged Rs. 1299.0 for ORD-2
  Order ORD-2 confirmed
Checking out ORD-3
  [Stripe] charged Rs. 1299.0 for ORD-3
  Order ORD-3 confirmed

=== Testing with a fake gateway ===
Checking out ORD-TEST
  [Fake] pretending to charge 500.0 -> declined
  Payment failed for ORD-TEST
Test result: checkout returned false, gateway was called 1 time(s) -> TEST PASSED (declined payment handled)
*/
