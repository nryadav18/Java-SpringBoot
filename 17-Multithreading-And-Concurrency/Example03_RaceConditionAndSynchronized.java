/*
 * Example03_RaceConditionAndSynchronized.java
 * ------------------------------------------------------------
 * What it shows:
 *   - A RACE CONDITION: two threads update the same variable at the same time
 *     and some updates get LOST.
 *   - The fix with the 'synchronized' keyword (only one thread at a time
 *     can be inside a synchronized method of the same object).
 *
 * Domain: a website "likes" counter. Two threads each click "like" 100,000 times.
 *         We expect 200,000 likes.
 *
 * How to run:
 *   java Example03_RaceConditionAndSynchronized.java
 */
public class Example03_RaceConditionAndSynchronized {

    // ---------- UNSAFE counter ----------
    static class UnsafeLikeCounter {
        private int likes = 0;

        void like() {
            // likes++ LOOKS like one step, but the CPU does 3 steps:
            //   1) read likes   2) add 1   3) write likes back
            // If two threads read the SAME old value, one "+1" is lost.
            likes++;
        }

        int getLikes() {
            return likes;
        }
    }

    // ---------- SAFE counter ----------
    static class SafeLikeCounter {
        private int likes = 0;

        // synchronized = this method has a lock (like a single bathroom key).
        // A thread must take the key (the object's "monitor lock") to enter.
        // Other threads wait outside until the key is returned.
        synchronized void like() {
            likes++;
        }

        synchronized int getLikes() {
            return likes;
        }
    }

    // Alternative style: a synchronized BLOCK that locks only the critical part.
    static class SafeWithBlock {
        private final Object lock = new Object();  // a private lock object
        private int likes = 0;

        void like() {
            // ... code here can run in parallel (e.g. logging, validation) ...
            synchronized (lock) {   // only this tiny part is protected
                likes++;
            }
        }

        int getLikes() {
            synchronized (lock) {
                return likes;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int CLICKS = 100_000;

        // ----- 1) Unsafe -----
        UnsafeLikeCounter unsafe = new UnsafeLikeCounter();
        Runnable unsafeTask = () -> {
            for (int i = 0; i < CLICKS; i++) unsafe.like();
        };
        Thread a = new Thread(unsafeTask);
        Thread b = new Thread(unsafeTask);
        a.start(); b.start();
        a.join();  b.join();
        System.out.println("Unsafe counter  : " + unsafe.getLikes() + " (expected 200000)");

        // ----- 2) synchronized method -----
        SafeLikeCounter safe = new SafeLikeCounter();
        Runnable safeTask = () -> {
            for (int i = 0; i < CLICKS; i++) safe.like();
        };
        Thread c = new Thread(safeTask);
        Thread d = new Thread(safeTask);
        c.start(); d.start();
        c.join();  d.join();
        System.out.println("Synchronized    : " + safe.getLikes() + " (expected 200000)");

        // ----- 3) synchronized block -----
        SafeWithBlock block = new SafeWithBlock();
        Runnable blockTask = () -> {
            for (int i = 0; i < CLICKS; i++) block.like();
        };
        Thread e = new Thread(blockTask);
        Thread f = new Thread(blockTask);
        e.start(); f.start();
        e.join();  f.join();
        System.out.println("Synchronized blk: " + block.getLikes() + " (expected 200000)");
    }
}

/*
 * Output (the first number is DIFFERENT on every run - that is the bug!):
 * Unsafe counter  : 137452 (expected 200000)
 * Synchronized    : 200000 (expected 200000)
 * Synchronized blk: 200000 (expected 200000)
 *
 * On a very fast or single-core machine the unsafe number might sometimes be
 * 200000 by luck. A bug that appears "sometimes" is still a bug.
 */
