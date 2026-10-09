import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Example07_ConcurrentHashMapAndAtomic.java
 * ------------------------------------------------------------
 * What it shows:
 *   Thread-safe building blocks from java.util.concurrent, so you often
 *   do NOT need to write 'synchronized' yourself:
 *     - AtomicInteger      : a number with thread-safe incrementAndGet()
 *     - ConcurrentHashMap  : a Map many threads can read/write safely,
 *                            with atomic helpers like merge() and computeIfAbsent()
 *   And WHY a normal HashMap is NOT safe with many threads.
 *
 * Domain: a website counting page visits from many users at the same time.
 *
 * How to run:
 *   java Example07_ConcurrentHashMapAndAtomic.java
 */
public class Example07_ConcurrentHashMapAndAtomic {

    public static void main(String[] args) throws InterruptedException {
        String[] pages = {"/home", "/products", "/cart"};
        int visitsPerPage = 10_000;

        // ---------- 1) AtomicInteger: a thread-safe counter, no locks needed ----------
        AtomicInteger totalVisits = new AtomicInteger(0);

        // ---------- 2) ConcurrentHashMap vs HashMap ----------
        Map<String, Integer> safeMap = new ConcurrentHashMap<>();
        Map<String, Integer> unsafeMap = new HashMap<>();

        ExecutorService pool = Executors.newFixedThreadPool(8);
        for (String page : pages) {
            for (int i = 0; i < visitsPerPage; i++) {
                pool.submit(() -> {
                    totalVisits.incrementAndGet();          // atomic "+1"

                    // merge(key, 1, Integer::sum) = "if absent put 1, else add 1"
                    // On ConcurrentHashMap this whole read-modify-write is ATOMIC.
                    safeMap.merge(page, 1, Integer::sum);

                    // Same call on a HashMap is NOT thread-safe -> lost updates.
                    // (We wrap it in try/catch because a HashMap used by many threads
                    //  can even throw exceptions or get corrupted.)
                    try {
                        unsafeMap.merge(page, 1, Integer::sum);
                    } catch (RuntimeException ignored) {
                        // ignored only for the demo
                    }
                });
            }
        }
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("Total visits (AtomicInteger): " + totalVisits.get() + " (expected 30000)");
        for (String page : pages) {
            System.out.printf("%-10s ConcurrentHashMap=%d   HashMap=%s%n",
                    page, safeMap.get(page), unsafeMap.get(page));
        }

        // ---------- 3) Other useful atomic methods ----------
        AtomicInteger stock = new AtomicInteger(5);
        // compareAndSet(expected, newValue): only change it if it still has the expected value.
        boolean changed = stock.compareAndSet(5, 4);
        System.out.println("\ncompareAndSet(5,4) worked? " + changed + " -> stock=" + stock.get());
        boolean changedAgain = stock.compareAndSet(5, 3); // fails, value is 4 now
        System.out.println("compareAndSet(5,3) worked? " + changedAgain + " -> stock=" + stock.get());
        System.out.println("addAndGet(10) -> " + stock.addAndGet(10));

        // ---------- 4) computeIfAbsent: a simple thread-safe cache ----------
        Map<Integer, String> userCache = new ConcurrentHashMap<>();
        String u1 = userCache.computeIfAbsent(101, id -> loadUserFromDb(id)); // loads
        String u2 = userCache.computeIfAbsent(101, id -> loadUserFromDb(id)); // from cache
        System.out.println(u1 + " / " + u2);
    }

    static String loadUserFromDb(int id) {
        System.out.println("  (loading user " + id + " from database...)");
        return "User#" + id;
    }
}

/*
 * Output (HashMap numbers are random and usually WRONG - that is the lesson):
 * Total visits (AtomicInteger): 30000 (expected 30000)
 * /home      ConcurrentHashMap=10000   HashMap=9873
 * /products  ConcurrentHashMap=10000   HashMap=9911
 * /cart      ConcurrentHashMap=10000   HashMap=9790
 *
 * compareAndSet(5,4) worked? true -> stock=4
 * compareAndSet(5,3) worked? false -> stock=4
 * addAndGet(10) -> 14
 *   (loading user 101 from database...)
 * User#101 / User#101
 */
