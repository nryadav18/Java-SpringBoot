/*
 * Example07_ExceptionChaining.java
 *
 * What this file demonstrates:
 *   - Exception CHAINING (wrapping): catch a low-level exception and throw a higher-level,
 *     more meaningful one, while KEEPING the original as the "cause".
 *       throw new OrderProcessingException("Could not place order 42", e);   // e = the cause
 *   - getCause() to walk down the chain
 *   - The "Caused by:" section in a stack trace
 *   - The WRONG way (losing the cause) for comparison
 *
 * Why? Each layer speaks its own language:
 *   repository layer: "SQL/connection failed"  ->  service layer: "Could not place order"
 *   The caller gets a clear message, and developers still see the root cause in the logs.
 *
 * How to run:
 *   java Example07_ExceptionChaining.java
 *
 * Output (the stack trace goes to the error stream):
 *   Problem   : Could not place order 42
 *   Caused by : Database connection refused (port 5432)
 *   Root cause type: java.io.IOException
 *   --- Full stack trace ---
 *   OrderProcessingException: Could not place order 42
 *   	at OrderService.placeOrder(Example07_ExceptionChaining.java:100)
 *   	at Example07_ExceptionChaining.main(Example07_ExceptionChaining.java:44)
 *   	at java.base/jdk.internal.reflect...   (+ a few JDK launcher lines - ignore them)
 *   Caused by: java.io.IOException: Database connection refused (port 5432)
 *   	at OrderRepository.save(Example07_ExceptionChaining.java:87)
 *   	at OrderService.placeOrder(Example07_ExceptionChaining.java:97)
 *   	... 8 more
 *   --- Wrong way: cause lost ---
 *   Problem: Could not place order 43
 *   Cause  : null   <- the real reason is gone forever!
 */
import java.io.IOException;

public class Example07_ExceptionChaining {

    public static void main(String[] args) {
        OrderService service = new OrderService();

        try {
            service.placeOrder(42);
        } catch (OrderProcessingException e) {
            System.out.println("Problem   : " + e.getMessage());
            System.out.println("Caused by : " + e.getCause().getMessage());

            // Walk down the chain to the ROOT cause (the very first exception)
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            System.out.println("Root cause type: " + root.getClass().getName());

            System.out.println("--- Full stack trace ---");
            System.out.flush();
            e.printStackTrace();
            System.err.flush();
        }

        System.out.println("--- Wrong way: cause lost ---");
        try {
            service.placeOrderBadly(43);
        } catch (OrderProcessingException e) {
            System.out.println("Problem: " + e.getMessage());
            System.out.println("Cause  : " + e.getCause() + "   <- the real reason is gone forever!");
        }
    }
}

// A high-level, business-friendly exception (unchecked)
class OrderProcessingException extends RuntimeException {
    OrderProcessingException(String message) {
        super(message);
    }

    OrderProcessingException(String message, Throwable cause) {
        super(message, cause);   // <-- this is what makes the chain
    }
}

// Low-level layer: talks to the "database"
class OrderRepository {
    void save(int orderId) throws IOException {
        // Simulate the database being down
        throw new IOException("Database connection refused (port 5432)");
    }
}

// Higher-level layer: business logic
class OrderService {
    private final OrderRepository repository = new OrderRepository();

    void placeOrder(int orderId) {
        try {
            repository.save(orderId);
        } catch (IOException e) {
            // RIGHT: wrap + pass the original exception as the cause
            throw new OrderProcessingException("Could not place order " + orderId, e);
        }
    }

    void placeOrderBadly(int orderId) {
        try {
            repository.save(orderId);
        } catch (IOException e) {
            // WRONG: the original exception "e" is thrown away
            throw new OrderProcessingException("Could not place order " + orderId);
        }
    }
}
