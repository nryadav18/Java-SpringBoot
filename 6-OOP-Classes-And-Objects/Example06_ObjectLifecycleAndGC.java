/*
 * Example06_ObjectLifecycleAndGC.java
 * ------------------------------------------------------------
 * What this file shows:
 *   The life of an object:  created (new) -> used -> unreachable -> garbage collected.
 *   Three ways an object becomes UNREACHABLE (eligible for garbage collection):
 *     a) set the reference to null
 *     b) point the reference to another object
 *     c) the reference was a local variable and the method ended
 *   We also watch heap memory go up when we create many objects and go down after GC.
 *
 * IMPORTANT: System.gc() is only a REQUEST. The JVM decides when to really clean up.
 * Memory numbers will be different on your computer - look at the trend, not the exact values.
 *
 * How to run (from this folder):
 *   java Example06_ObjectLifecycleAndGC.java
 */
public class Example06_ObjectLifecycleAndGC {

    public static void main(String[] args) {

        // ---------- the three ways ----------
        Ticket t1 = new Ticket(1);       // created, reachable through t1
        t1 = null;                       // a) no variable points to Ticket #1 anymore -> eligible for GC

        Ticket t2 = new Ticket(2);
        t2 = new Ticket(3);              // b) Ticket #2 lost its only reference -> eligible for GC
        System.out.println("t2 now points to ticket #" + t2.number);

        bookTemporaryTicket();           // c) the Ticket created inside is unreachable after the method returns
        System.out.println("Ticket objects created so far: " + Ticket.created);

        // ---------- watching memory ----------
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        long before = usedMemoryMb(runtime);

        Ticket[] lots = new Ticket[1_000_000];       // one million objects, all reachable via the array
        for (int i = 0; i < lots.length; i++) {
            lots[i] = new Ticket(i);
        }
        long during = usedMemoryMb(runtime);

        lots = null;                                 // the array (and all tickets) become unreachable
        System.gc();                                 // politely ask the JVM to clean up
        long after = usedMemoryMb(runtime);

        System.out.println("Memory went UP while 1,000,000 tickets were reachable: " + (during > before));
        System.out.println("Memory went DOWN after they became unreachable + GC: " + (after < during));
        System.out.println("(approx MB used - before: " + before + ", during: " + during + ", after: " + after + ")");
    }

    static void bookTemporaryTicket() {
        Ticket temp = new Ticket(99);    // 'temp' lives on the STACK only while this method runs
        System.out.println("Using temporary ticket #" + temp.number);
    }   // method ends -> 'temp' disappears -> Ticket #99 is unreachable

    static long usedMemoryMb(Runtime runtime) {
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
    }
}

class Ticket {
    // 'static' = ONE counter shared by the whole class, not one per object (full story in Topic 9).
    static int created = 0;

    int number;

    Ticket(int number) {
        this.number = number;
        created++;
    }
}

/*
Output:
t2 now points to ticket #3
Using temporary ticket #99
Ticket objects created so far: 4
Memory went UP while 1,000,000 tickets were reachable: true
Memory went DOWN after they became unreachable + GC: true
(approx MB used - before: 2, during: 23, after: 2)   <- your numbers will differ
*/
