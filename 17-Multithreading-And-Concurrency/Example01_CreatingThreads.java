/*
 * Example01_CreatingThreads.java
 * ------------------------------------------------------------
 * What it shows:
 *   The 3 classic ways to create a thread in Java:
 *     1) extend the Thread class
 *     2) implement the Runnable interface and give it to a Thread
 *     3) use a lambda (a short Runnable)  <-- what you will write most often
 *   It also shows that start() runs code in a NEW thread, while
 *   calling run() directly just runs it in the CURRENT thread.
 *
 * How to run (from this folder):
 *   java Example01_CreatingThreads.java
 *
 * NOTE: thread output order can change from run to run.
 *       That is normal - the operating system decides who runs when.
 */
public class Example01_CreatingThreads {

    // ---------- Way 1: extend Thread ----------
    // We create a new kind of Thread and override run().
    // run() = "the job this thread should do".
    static class DownloadThread extends Thread {
        private final String fileName;

        DownloadThread(String fileName) {
            super("downloader");          // give the thread a readable name
            this.fileName = fileName;
        }

        @Override
        public void run() {
            // Thread.currentThread() tells us WHICH thread is executing this line.
            System.out.println("[" + Thread.currentThread().getName() + "] downloading " + fileName);
        }
    }

    // ---------- Way 2: implement Runnable ----------
    // Runnable is just "a task". It is NOT a thread by itself.
    // We hand the task to a Thread (the worker) to execute it.
    // This is preferred over Way 1: our class is free to extend something else,
    // and the TASK is separate from the WORKER.
    static class EmailTask implements Runnable {
        private final String to;

        EmailTask(String to) {
            this.to = to;
        }

        @Override
        public void run() {
            System.out.println("[" + Thread.currentThread().getName() + "] sending email to " + to);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Every Java program starts with ONE thread called "main".
        System.out.println("Program starts in thread: " + Thread.currentThread().getName());

        // Way 1
        Thread t1 = new DownloadThread("report.pdf");

        // Way 2: new Thread(task, name)
        Thread t2 = new Thread(new EmailTask("riya@example.com"), "emailer");

        // Way 3: lambda. Runnable has ONE method (run), so a lambda fits perfectly.
        Thread t3 = new Thread(() ->
                System.out.println("[" + Thread.currentThread().getName() + "] resizing photo.jpg"),
                "photo-worker");

        // start() asks the JVM to create a real new thread and call run() THERE.
        t1.start();
        t2.start();
        t3.start();

        // join() = "main, please wait here until this thread has finished".
        // Without join(), main might print its last line before the workers finish.
        t1.join();
        t2.join();
        t3.join();

        // ---------- The classic mistake: calling run() instead of start() ----------
        Thread wrong = new Thread(() ->
                System.out.println("[" + Thread.currentThread().getName() + "] I was called with run()"),
                "never-started");
        wrong.run();   // NO new thread! This line runs inside "main".

        System.out.println("All work done. Main thread ends.");
    }
}

/*
 * Output (the middle 3 lines may appear in a different order on your machine):
 * Program starts in thread: main
 * [downloader] downloading report.pdf
 * [emailer] sending email to riya@example.com
 * [photo-worker] resizing photo.jpg
 * [main] I was called with run()
 * All work done. Main thread ends.
 */
