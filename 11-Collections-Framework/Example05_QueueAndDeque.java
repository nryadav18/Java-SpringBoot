/*
 * Example05_QueueAndDeque.java
 *
 * What this file demonstrates:
 *   - Queue  : FIFO (First In, First Out) - like a line at a ticket counter.
 *              offer() to add at the back, poll() to take from the front, peek() to look.
 *   - Deque  : "double-ended queue" - add/remove at BOTH ends.
 *              ArrayDeque is the recommended class for BOTH a queue AND a stack.
 *   - Stack  : LIFO (Last In, First Out) - like a pile of plates. push() / pop() / peek().
 *              (Use ArrayDeque, not the old java.util.Stack class.)
 *   - PriorityQueue : always gives you the SMALLEST (highest-priority) element first.
 *   - Exception-throwing vs null-returning methods (add/remove/element vs offer/poll/peek).
 *
 * Domain: food-delivery orders, browser back button, hospital emergency room.
 *
 * How to run:
 *   java Example05_QueueAndDeque.java
 *
 * Output:
 *   --- Queue (FIFO) ---
 *   Waiting orders: [ORD-1, ORD-2, ORD-3]
 *   Next to cook (peek): ORD-1
 *   Cooking: ORD-1
 *   Cooking: ORD-2
 *   Cooking: ORD-3
 *   poll() on empty queue: null
 *   remove() on empty queue throws: NoSuchElementException
 *   --- Stack (LIFO) with ArrayDeque ---
 *   History: [cart, product, home]
 *   Back from: cart
 *   Back from: product
 *   Now on: home
 *   --- Deque: both ends ---
 *   [VIP-guest, regular-1, regular-2, late-guest]
 *   first=VIP-guest last=late-guest
 *   --- PriorityQueue ---
 *   Treating: Patient{name=Meena, severity=1}
 *   Treating: Patient{name=Ravi, severity=2}
 *   Treating: Patient{name=Asha, severity=3}
 *   Treating: Patient{name=Dev, severity=5}
 *   Smallest numbers first: 3 7 12 25
 */
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.Queue;

public class Example05_QueueAndDeque {

    public static void main(String[] args) {
        System.out.println("--- Queue (FIFO) ---");
        Queue<String> kitchen = new ArrayDeque<>();
        kitchen.offer("ORD-1");     // add at the back
        kitchen.offer("ORD-2");
        kitchen.offer("ORD-3");
        System.out.println("Waiting orders: " + kitchen);
        System.out.println("Next to cook (peek): " + kitchen.peek());   // look, don't remove
        while (!kitchen.isEmpty()) {
            System.out.println("Cooking: " + kitchen.poll());           // take from the front
        }
        System.out.println("poll() on empty queue: " + kitchen.poll()); // null, no exception
        try {
            kitchen.remove();                                           // same as poll, but THROWS when empty
        } catch (java.util.NoSuchElementException e) {
            System.out.println("remove() on empty queue throws: " + e.getClass().getSimpleName());
        }

        System.out.println("--- Stack (LIFO) with ArrayDeque ---");
        Deque<String> history = new ArrayDeque<>();
        history.push("home");       // push = add on TOP
        history.push("product");
        history.push("cart");
        System.out.println("History: " + history);              // top is printed first
        System.out.println("Back from: " + history.pop());      // pop = remove from TOP
        System.out.println("Back from: " + history.pop());
        System.out.println("Now on: " + history.peek());

        System.out.println("--- Deque: both ends ---");
        Deque<String> line = new ArrayDeque<>();
        line.offerLast("regular-1");
        line.offerLast("regular-2");
        line.offerFirst("VIP-guest");    // jumps to the front
        line.offerLast("late-guest");
        System.out.println(line);
        System.out.println("first=" + line.peekFirst() + " last=" + line.peekLast());

        System.out.println("--- PriorityQueue ---");
        // Patients are ordered by severity: 1 = most urgent. Patient implements Comparable (see Example07).
        PriorityQueue<Patient> emergency = new PriorityQueue<>();
        emergency.offer(new Patient("Asha", 3));
        emergency.offer(new Patient("Dev", 5));
        emergency.offer(new Patient("Meena", 1));
        emergency.offer(new Patient("Ravi", 2));
        while (!emergency.isEmpty()) {
            System.out.println("Treating: " + emergency.poll());
        }

        PriorityQueue<Integer> numbers = new PriorityQueue<>();
        numbers.offer(25);
        numbers.offer(7);
        numbers.offer(12);
        numbers.offer(3);
        System.out.print("Smallest numbers first:");
        while (!numbers.isEmpty()) {
            System.out.print(" " + numbers.poll());
        }
        System.out.println();
        // Note: printing a PriorityQueue directly (System.out.println(numbers)) does NOT show
        // sorted order - only poll() guarantees the order.
    }
}

class Patient implements Comparable<Patient> {
    private final String name;
    private final int severity;

    Patient(String name, int severity) {
        this.name = name;
        this.severity = severity;
    }

    // Smaller severity = more urgent = comes out of the PriorityQueue first
    @Override
    public int compareTo(Patient other) {
        return Integer.compare(this.severity, other.severity);
    }

    @Override
    public String toString() {
        return "Patient{name=" + name + ", severity=" + severity + "}";
    }
}
