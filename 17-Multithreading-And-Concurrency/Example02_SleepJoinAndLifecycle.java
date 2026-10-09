/*
 * Example02_SleepJoinAndLifecycle.java
 * ------------------------------------------------------------
 * What it shows:
 *   - The life of a thread: NEW -> RUNNABLE -> TIMED_WAITING/WAITING/BLOCKED -> TERMINATED
 *   - Thread.sleep(ms)  : pause the CURRENT thread for some time
 *   - t.join()          : wait for ANOTHER thread to finish
 *   - t.getState()      : ask a thread which state it is in
 *   - interrupt()       : politely ask a sleeping/waiting thread to stop
 *
 * How to run:
 *   java Example02_SleepJoinAndLifecycle.java
 */
public class Example02_SleepJoinAndLifecycle {

    public static void main(String[] args) throws InterruptedException {

        // A "chef" thread that cooks for 300 ms (simulated with sleep).
        Thread chef = new Thread(() -> {
            try {
                System.out.println("Chef: started cooking pasta...");
                Thread.sleep(300);           // the chef thread pauses here (TIMED_WAITING)
                System.out.println("Chef: pasta is ready!");
            } catch (InterruptedException e) {
                // sleep() throws this checked exception if someone interrupts us.
                System.out.println("Chef: I was interrupted!");
            }
        }, "chef");

        // 1) Created, but not started yet -> NEW
        System.out.println("1. State after new Thread(): " + chef.getState());

        chef.start();
        // 2) Started -> RUNNABLE (ready to run or running)
        System.out.println("2. State right after start(): " + chef.getState());

        Thread.sleep(100); // main waits a little so the chef is surely sleeping now
        // 3) The chef is inside sleep(300) -> TIMED_WAITING
        System.out.println("3. State while chef sleeps: " + chef.getState());

        chef.join();       // main waits until the chef thread has finished
        // 4) run() has finished -> TERMINATED
        System.out.println("4. State after join(): " + chef.getState());

        // ---------- interrupt() demo ----------
        Thread sleepy = new Thread(() -> {
            try {
                System.out.println("Sleepy: going to sleep for 10 seconds...");
                Thread.sleep(10_000);
                System.out.println("Sleepy: woke up normally");   // will NOT print
            } catch (InterruptedException e) {
                System.out.println("Sleepy: someone woke me up early (interrupted)!");
            }
        }, "sleepy");

        sleepy.start();
        Thread.sleep(100);   // let it fall asleep
        sleepy.interrupt();  // knock on the door
        sleepy.join();

        System.out.println("Main: done.");
    }
}

/*
 * Output:
 * 1. State after new Thread(): NEW
 * 2. State right after start(): RUNNABLE
 * Chef: started cooking pasta...
 * 3. State while chef sleeps: TIMED_WAITING
 * Chef: pasta is ready!
 * 4. State after join(): TERMINATED
 * Sleepy: going to sleep for 10 seconds...
 * Sleepy: someone woke me up early (interrupted)!
 * Main: done.
 *
 * (Line 2 and "Chef: started cooking" may swap places - the threads race.)
 */
