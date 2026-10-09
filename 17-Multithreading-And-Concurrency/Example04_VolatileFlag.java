/*
 * Example04_VolatileFlag.java
 * ------------------------------------------------------------
 * What it shows:
 *   The 'volatile' keyword = "always read this variable from main memory,
 *   never from a private cached copy". It solves VISIBILITY problems.
 *
 *   Typical use: a stop flag. One thread loops "while (running)",
 *   another thread sets running = false to stop it.
 *   Without volatile, the looping thread MAY keep a cached copy of 'true'
 *   and never see the change (the program could hang forever).
 *
 *   volatile does NOT make count++ safe (that needs synchronized or Atomic*).
 *
 * How to run:
 *   java Example04_VolatileFlag.java
 */
public class Example04_VolatileFlag {

    // A background worker that "processes orders" until told to stop.
    static class OrderProcessor implements Runnable {

        // volatile: every write by one thread is immediately visible to others.
        private volatile boolean running = true;
        private long processed = 0;

        @Override
        public void run() {
            System.out.println("Processor: started");
            while (running) {          // reads the fresh value each time
                processed++;           // pretend to process one order
            }
            System.out.println("Processor: stopped after processing many orders (" + (processed > 0) + ")");
        }

        void stop() {
            running = false;           // written by the main thread
        }
    }

    public static void main(String[] args) throws InterruptedException {
        OrderProcessor processor = new OrderProcessor();
        Thread worker = new Thread(processor, "order-processor");
        worker.start();

        Thread.sleep(200);             // let it work for a moment
        System.out.println("Main: asking processor to stop...");
        processor.stop();

        worker.join();                 // without volatile this could wait forever
        System.out.println("Main: processor has finished. Bye!");
    }
}

/*
 * Output:
 * Processor: started
 * Main: asking processor to stop...
 * Processor: stopped after processing many orders (true)
 * Main: processor has finished. Bye!
 *
 * Try it: remove 'volatile' and run a few times. On many machines the program
 * will HANG at "Main: asking processor to stop..." (press Ctrl+C to quit).
 */
