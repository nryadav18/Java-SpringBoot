/*
 * Example08_DeadlockDemo.java
 * ------------------------------------------------------------
 * What it shows:
 *   A DEADLOCK: two threads each hold one lock and wait forever for the other's lock.
 *     Thread 1: locks A, then wants B
 *     Thread 2: locks B, then wants A
 *   Both wait for each other -> nobody can continue. Forever.
 *
 *   The fix: ALWAYS take locks in the SAME ORDER (e.g. smaller account id first).
 *
 *   To keep this demo SAFE (no hanging program), the deadlocked threads are
 *   "daemon" threads: the JVM is allowed to exit even if they are stuck.
 *   We use join(timeout) to detect that they are stuck.
 *
 * Domain: bank transfers between two accounts.
 *
 * How to run:
 *   java Example08_DeadlockDemo.java
 */
public class Example08_DeadlockDemo {

    static class Account {
        final int id;
        int balance;

        Account(int id, int balance) {
            this.id = id;
            this.balance = balance;
        }
    }

    // ---------- BAD transfer: lock order depends on the arguments ----------
    static void transferBad(Account from, Account to, int amount) {
        synchronized (from) {                       // lock 1
            pause(100);                             // makes the deadlock very likely
            synchronized (to) {                     // lock 2
                from.balance -= amount;
                to.balance += amount;
            }
        }
    }

    // ---------- GOOD transfer: always lock the smaller id first ----------
    static void transferGood(Account from, Account to, int amount) {
        Account first  = from.id < to.id ? from : to;
        Account second = from.id < to.id ? to : from;
        synchronized (first) {
            pause(100);
            synchronized (second) {
                from.balance -= amount;
                to.balance += amount;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // ----- 1) The deadlock -----
        Account a = new Account(1, 1000);
        Account b = new Account(2, 1000);

        Thread t1 = new Thread(() -> transferBad(a, b, 100), "T1 (A->B)");
        Thread t2 = new Thread(() -> transferBad(b, a, 50),  "T2 (B->A)");
        t1.setDaemon(true);   // daemon: will not keep the JVM alive
        t2.setDaemon(true);
        t1.start();
        t2.start();

        t1.join(1000);        // wait at most 1 second
        t2.join(1000);

        if (t1.isAlive() && t2.isAlive()) {
            System.out.println("DEADLOCK! " + t1.getName() + " is " + t1.getState()
                    + " and " + t2.getName() + " is " + t2.getState());
            System.out.println("Each thread holds one account lock and waits for the other.");
        }

        // ----- 2) The fix: consistent lock ordering -----
        Account c = new Account(1, 1000);
        Account d = new Account(2, 1000);
        Thread t3 = new Thread(() -> transferGood(c, d, 100), "T3 (C->D)");
        Thread t4 = new Thread(() -> transferGood(d, c, 50),  "T4 (D->C)");
        t3.start();
        t4.start();
        t3.join();
        t4.join();
        System.out.println("\nWith lock ordering: both transfers finished.");
        System.out.println("Account 1 balance = " + c.balance + ", Account 2 balance = " + d.balance);
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
 * Output:
 * DEADLOCK! T1 (A->B) is BLOCKED and T2 (B->A) is BLOCKED
 * Each thread holds one account lock and waits for the other.
 *
 * With lock ordering: both transfers finished.
 * Account 1 balance = 950, Account 2 balance = 1050
 */
