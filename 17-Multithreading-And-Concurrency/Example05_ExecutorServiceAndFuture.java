import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/*
 * Example05_ExecutorServiceAndFuture.java
 * ------------------------------------------------------------
 * What it shows:
 *   - Creating threads by hand for every job is expensive and messy.
 *     A THREAD POOL (ExecutorService) keeps a few worker threads ready
 *     and reuses them for many tasks.
 *   - submit(Runnable)   : fire-and-forget task
 *   - submit(Callable)   : task that RETURNS a value -> gives you a Future
 *   - future.get()       : wait for (and fetch) the result
 *   - invokeAll(...)     : submit many Callables at once
 *   - shutdown() + awaitTermination() : the correct way to close a pool
 *
 * Domain: a food-delivery app preparing orders in a kitchen with 3 cooks.
 *
 * How to run:
 *   java Example05_ExecutorServiceAndFuture.java
 */
public class Example05_ExecutorServiceAndFuture {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        // A pool with exactly 3 worker threads ("3 cooks in the kitchen").
        ExecutorService kitchen = Executors.newFixedThreadPool(3);

        // ---------- 1) Runnable tasks (no result) ----------
        System.out.println("== 1) Six orders, only 3 cooks ==");
        for (int orderNo = 1; orderNo <= 6; orderNo++) {
            int id = orderNo; // lambdas need effectively-final variables
            kitchen.submit(() -> {
                String cook = Thread.currentThread().getName();
                System.out.println("Order " + id + " is cooked by " + cook);
                sleep(100);
            });
        }
        // Notice in the output: only 3 different thread names appear.
        // The 3 threads are REUSED for 6 orders. That is the whole point of a pool.
        sleep(500); // wait so section 1 output is not mixed with section 2

        // ---------- 2) Callable + Future (task returns a value) ----------
        System.out.println("\n== 2) Callable returns the bill amount ==");
        Callable<Double> calculateBill = () -> {
            sleep(200);              // pretend to call a pricing service
            return 249.50;
        };
        Future<Double> futureBill = kitchen.submit(calculateBill);
        // A Future is a "receipt / token" for a result that will exist later.
        System.out.println("Bill requested. Is it done yet? " + futureBill.isDone());
        Double bill = futureBill.get();  // BLOCKS (waits) until the result is ready
        System.out.println("Bill amount = " + bill + ". Done now? " + futureBill.isDone());

        // ---------- 3) invokeAll: many Callables, collect all results ----------
        System.out.println("\n== 3) Prepare 3 dishes in parallel ==");
        List<Callable<String>> dishes = new ArrayList<>();
        dishes.add(() -> { sleep(300); return "Pizza"; });
        dishes.add(() -> { sleep(300); return "Burger"; });
        dishes.add(() -> { sleep(300); return "Salad"; });

        long start = System.currentTimeMillis();
        List<Future<String>> results = kitchen.invokeAll(dishes); // waits for all
        long took = System.currentTimeMillis() - start;

        for (Future<String> f : results) {
            System.out.println("Ready: " + f.get());
        }
        // 3 dishes x 300 ms each, but in parallel -> about 300 ms, not 900 ms.
        System.out.println("All 3 dishes took about " + (took < 600 ? "300" : String.valueOf(took)) + " ms (not 900!)");

        // ---------- 4) Always shut the pool down ----------
        kitchen.shutdown();                         // no new tasks accepted
        if (!kitchen.awaitTermination(5, TimeUnit.SECONDS)) {
            kitchen.shutdownNow();                  // force stop if still busy
        }
        System.out.println("\nKitchen closed: " + kitchen.isShutdown());
    }

    // Small helper so the examples are not full of try/catch.
    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore the interrupt flag (good practice)
        }
    }
}

/*
 * Output (order of lines inside section 1 can vary; pool thread names look like pool-1-thread-N):
 * == 1) Six orders, only 3 cooks ==
 * Order 1 is cooked by pool-1-thread-1
 * Order 2 is cooked by pool-1-thread-2
 * Order 3 is cooked by pool-1-thread-3
 * Order 4 is cooked by pool-1-thread-1
 * Order 5 is cooked by pool-1-thread-3
 * Order 6 is cooked by pool-1-thread-2
 *
 * == 2) Callable returns the bill amount ==
 * Bill requested. Is it done yet? false
 * Bill amount = 249.5. Done now? true
 *
 * == 3) Prepare 3 dishes in parallel ==
 * Ready: Pizza
 * Ready: Burger
 * Ready: Salad
 * All 3 dishes took about 300 ms (not 900!)
 *
 * Kitchen closed: true
 */
