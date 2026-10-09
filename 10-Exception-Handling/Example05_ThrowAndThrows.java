/*
 * Example05_ThrowAndThrows.java
 *
 * What this file demonstrates:
 *   - throw  : a STATEMENT that throws ONE exception object right now.
 *              throw new IllegalArgumentException("Age must be 18+");
 *   - throws : part of a METHOD SIGNATURE that WARNS callers which (checked) exceptions may come out.
 *              void load() throws IOException
 *   - How an exception travels UP the call stack (propagation) until someone catches it.
 *   - Validating input early ("fail fast") with the standard JDK exceptions.
 *
 * Domain: a movie-ticket booking.
 *
 * How to run:
 *   java Example05_ThrowAndThrows.java
 *
 * Output:
 *   Booked 2 seat(s) for Asha (age 25)
 *   Rejected: Age must be 18 or more for an A-rated movie, got 15
 *   Rejected: Seats must be between 1 and 10, got 0
 *   Rejected: Name must not be empty
 *   --- Propagation: main -> bookingCounter -> paymentGateway ---
 *   paymentGateway: contacting bank...
 *   main caught it: Bank server not reachable
 *   bookingCounter's line after the call never ran
 */
import java.io.IOException;

public class Example05_ThrowAndThrows {

    public static void main(String[] args) {
        tryBooking("Asha", 25, 2);
        tryBooking("Kid", 15, 1);
        tryBooking("Ravi", 30, 0);
        tryBooking("", 30, 1);

        System.out.println("--- Propagation: main -> bookingCounter -> paymentGateway ---");
        try {
            bookingCounter();
        } catch (IOException e) {
            System.out.println("main caught it: " + e.getMessage());
            System.out.println("bookingCounter's line after the call never ran");
        }
    }

    static void tryBooking(String name, int age, int seats) {
        try {
            bookTicket(name, age, seats);
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }

    // Validate first, then do the work. If anything is wrong, THROW - don't continue with bad data.
    static void bookTicket(String name, int age, int seats) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        if (age < 18) {
            throw new IllegalArgumentException("Age must be 18 or more for an A-rated movie, got " + age);
        }
        if (seats < 1 || seats > 10) {
            throw new IllegalArgumentException("Seats must be between 1 and 10, got " + seats);
        }
        System.out.println("Booked " + seats + " seat(s) for " + name + " (age " + age + ")");
    }

    // bookingCounter does NOT catch the IOException, so it must DECLARE it with "throws".
    static void bookingCounter() throws IOException {
        paymentGateway();
        System.out.println("bookingCounter: payment done");   // skipped when an exception passes through
    }

    // IOException is CHECKED -> "throws IOException" is required here.
    static void paymentGateway() throws IOException {
        System.out.println("paymentGateway: contacting bank...");
        throw new IOException("Bank server not reachable");
    }
}
