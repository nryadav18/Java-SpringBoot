import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Example09_WebServerSimulation.java
 * ------------------------------------------------------------
 * What it shows:
 *   WHY multithreading matters for Spring Boot.
 *   A web server (like Tomcat inside Spring Boot) handles EACH HTTP request
 *   on a thread taken from a THREAD POOL (Tomcat's default max is 200 threads).
 *   That means:
 *     - many users are served at the same time
 *     - your @Service / @RestController objects (Spring singletons) are used by
 *       MANY threads at once -> they must NOT keep per-request data in fields.
 *
 *   We simulate a tiny server with a pool of 4 "request threads"
 *   handling 8 requests, and show a BAD controller with a shared field.
 *
 * How to run:
 *   java Example09_WebServerSimulation.java
 */
public class Example09_WebServerSimulation {

    // ---------- BAD: stores per-request data in a field of a shared object ----------
    static class BadGreetingController {
        private String currentUser;            // shared by ALL threads -> danger!

        String greet(String user) {
            currentUser = user;                // thread A writes "Asha"...
            pause(50);                         // ...meanwhile thread B writes "Ben"
            return "Hello " + currentUser;     // thread A may now read "Ben"!
        }
    }

    // ---------- GOOD: stateless - uses only parameters/local variables ----------
    static class GoodGreetingController {
        // Shared, but thread-safe state is OK (e.g. an AtomicInteger counter).
        private final AtomicInteger requestCount = new AtomicInteger();

        String greet(String user) {
            requestCount.incrementAndGet();
            String message = "Hello " + user;  // local variable: each thread has its own
            pause(50);
            return message;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String[] users = {"Asha", "Ben", "Chen", "Divya", "Eli", "Fatima", "Gopal", "Hana"};

        // ONE controller object shared by all requests - exactly like a Spring singleton bean.
        BadGreetingController bad = new BadGreetingController();
        GoodGreetingController good = new GoodGreetingController();

        System.out.println("== BAD controller (shared mutable field) ==");
        runServer(users, user -> bad.greet(user));

        System.out.println("\n== GOOD controller (stateless) ==");
        runServer(users, user -> good.greet(user));
        System.out.println("Requests counted safely: " + good.requestCount.get());
    }

    // Our pretend "Tomcat": a pool of 4 request-handling threads.
    static void runServer(String[] users, java.util.function.Function<String, String> handler)
            throws InterruptedException {
        ExecutorService requestThreads = Executors.newFixedThreadPool(4);
        for (String user : users) {
            requestThreads.submit(() -> {
                String response = handler.apply(user);
                String status = response.equals("Hello " + user) ? "OK   " : "WRONG";
                System.out.printf("%s request from %-6s -> \"%s\" [%s]%n",
                        status, user, response, Thread.currentThread().getName());
            });
        }
        requestThreads.shutdown();
        requestThreads.awaitTermination(5, TimeUnit.SECONDS);
    }

    private static void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

/*
 * Output (order and the WRONG lines vary on every run):
 * == BAD controller (shared mutable field) ==
 * WRONG request from Asha   -> "Hello Divya" [pool-1-thread-1]
 * WRONG request from Ben    -> "Hello Divya" [pool-1-thread-2]
 * WRONG request from Chen   -> "Hello Divya" [pool-1-thread-3]
 * OK    request from Divya  -> "Hello Divya" [pool-1-thread-4]
 * ... (more lines, many WRONG)
 *
 * == GOOD controller (stateless) ==
 * OK    request from Asha   -> "Hello Asha" [pool-2-thread-1]
 * OK    request from Ben    -> "Hello Ben" [pool-2-thread-2]
 * ... (all 8 lines OK)
 * Requests counted safely: 8
 */
