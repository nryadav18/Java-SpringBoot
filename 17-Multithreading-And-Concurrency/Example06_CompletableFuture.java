import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Example06_CompletableFuture.java
 * ------------------------------------------------------------
 * What it shows:
 *   CompletableFuture = a Future you can CHAIN steps onto, without blocking.
 *     supplyAsync(...)   : start a task in the background that returns a value
 *     thenApply(...)     : transform the result when it arrives (like map)
 *     thenAccept(...)    : consume the result (no return value)
 *     thenCombine(...)   : combine TWO independent futures
 *     exceptionally(...) : provide a fallback value if something failed
 *     join()             : wait for the final result (like get(), but no checked exception)
 *     allOf(...)         : wait for many futures
 *
 * Domain: an online-shop "product page" that needs data from several services
 *         (product details, price, stock, reviews). We fetch them in PARALLEL.
 *
 * How to run:
 *   java Example06_CompletableFuture.java
 */
public class Example06_CompletableFuture {

    // ---- Fake "remote services" (each one is slow) ----
    static String fetchProductName(int id) {
        sleep(300);
        return "Wireless Mouse";
    }

    static double fetchPrice(int id) {
        sleep(300);
        return 799.0;
    }

    static int fetchStock(int id) {
        sleep(300);
        return 42;
    }

    static double fetchRating(int id) {
        sleep(300);
        throw new IllegalStateException("Review service is down!");
    }

    public static void main(String[] args) {
        // We use our own small pool. (Without it, supplyAsync uses the shared
        // ForkJoinPool.commonPool() - fine for demos, but in real apps prefer your own.)
        ExecutorService pool = Executors.newFixedThreadPool(4);

        // ---------- 1) supplyAsync + thenApply + join ----------
        System.out.println("== 1) Simple chain ==");
        String shout = CompletableFuture
                .supplyAsync(() -> fetchProductName(1), pool)  // runs in background
                .thenApply(name -> name.toUpperCase())        // runs when name arrives
                .thenApply(name -> "*** " + name + " ***")
                .join();                                       // wait for the final value
        System.out.println(shout);

        // ---------- 2) thenCombine: two calls in parallel ----------
        System.out.println("\n== 2) Name + price in parallel ==");
        long start = System.currentTimeMillis();

        CompletableFuture<String> nameF  = CompletableFuture.supplyAsync(() -> fetchProductName(1), pool);
        CompletableFuture<Double> priceF = CompletableFuture.supplyAsync(() -> fetchPrice(1), pool);

        String line = nameF
                .thenCombine(priceF, (name, price) -> name + " costs Rs." + price)
                .join();

        long took = System.currentTimeMillis() - start;
        System.out.println(line);
        // Each call takes 300 ms. In sequence: 600 ms. In parallel: ~300 ms.
        System.out.println("Took about " + (took < 550 ? "300" : String.valueOf(took)) + " ms (parallel), not 600 ms");

        // ---------- 3) exceptionally: fallback when a service fails ----------
        System.out.println("\n== 3) Handling a failing service ==");
        double rating = CompletableFuture
                .supplyAsync(() -> fetchRating(1), pool)
                .exceptionally(ex -> {
                    // ex is a CompletionException wrapping the real exception
                    System.out.println("Could not load rating: " + ex.getCause().getMessage());
                    return 0.0;                      // fallback value
                })
                .join();
        System.out.println("Rating shown on page: " + rating);

        // ---------- 4) allOf: build the whole product page ----------
        System.out.println("\n== 4) Whole product page ==");
        CompletableFuture<String>  n = CompletableFuture.supplyAsync(() -> fetchProductName(7), pool);
        CompletableFuture<Double>  p = CompletableFuture.supplyAsync(() -> fetchPrice(7), pool);
        CompletableFuture<Integer> s = CompletableFuture.supplyAsync(() -> fetchStock(7), pool);

        CompletableFuture.allOf(n, p, s).join();   // wait until ALL three are complete
        // Now join() on each returns immediately because they are already done.
        System.out.println("Product: " + n.join() + " | Price: " + p.join() + " | In stock: " + s.join());

        // thenAccept: do something with the result, return nothing
        CompletableFuture.supplyAsync(() -> fetchStock(7), pool)
                .thenAccept(stock -> System.out.println("Stock alert check: " + (stock < 10 ? "LOW" : "OK")))
                .join();

        pool.shutdown();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

/*
 * Output:
 * == 1) Simple chain ==
 * *** WIRELESS MOUSE ***
 *
 * == 2) Name + price in parallel ==
 * Wireless Mouse costs Rs.799.0
 * Took about 300 ms (parallel), not 600 ms
 *
 * == 3) Handling a failing service ==
 * Could not load rating: Review service is down!
 * Rating shown on page: 0.0
 *
 * == 4) Whole product page ==
 * Product: Wireless Mouse | Price: 799.0 | In stock: 42
 * Stock alert check: OK
 */
