/*
 * Example06_IteratorAndConcurrentModification.java
 *
 * What this file demonstrates:
 *   - Iterator: the "cursor" behind every for-each loop: hasNext() / next() / remove().
 *   - ConcurrentModificationException (CME): what happens if you remove from a list
 *     while looping over it with for-each - and WHY.
 *   - 3 correct ways to remove while looping:
 *       1) iterator.remove()
 *       2) removeIf(...)   (uses a tiny lambda - full explanation in Topic 13)
 *       3) loop backwards with an index
 *   - ListIterator: can also replace elements while looping.
 *
 * Domain: removing out-of-stock products from a shop's list.
 *
 * How to run:
 *   java Example06_IteratorAndConcurrentModification.java
 *
 * Output:
 *   Products: [Pen, OUT:Notebook, Bag, OUT:Ruler, Glue]
 *   --- Manual iterator ---
 *   Pen OUT:Notebook Bag OUT:Ruler Glue
 *   --- Wrong: remove inside for-each ---
 *   Boom! ConcurrentModificationException
 *   --- Right #1: iterator.remove() ---
 *   [Pen, Bag, Glue]
 *   --- Right #2: removeIf ---
 *   [Pen, Bag, Glue]
 *   --- Right #3: backwards index loop ---
 *   [Pen, Bag, Glue]
 *   --- ListIterator: change while looping ---
 *   [PEN, OUT:NOTEBOOK, BAG, OUT:RULER, GLUE]
 */
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Example06_IteratorAndConcurrentModification {

    static List<String> freshList() {
        return new ArrayList<>(List.of("Pen", "OUT:Notebook", "Bag", "OUT:Ruler", "Glue"));
    }

    public static void main(String[] args) {
        System.out.println("Products: " + freshList());

        System.out.println("--- Manual iterator ---");
        // This is exactly what "for (String p : products)" does behind the scenes.
        Iterator<String> it = freshList().iterator();
        while (it.hasNext()) {            // is there another element?
            String p = it.next();         // move forward and give me that element
            System.out.print(p + " ");
        }
        System.out.println();

        System.out.println("--- Wrong: remove inside for-each ---");
        List<String> products = freshList();
        try {
            for (String p : products) {
                if (p.startsWith("OUT:")) {
                    products.remove(p);   // changes the list BEHIND the hidden iterator's back
                }
            }
        } catch (ConcurrentModificationException e) {
            // The list keeps a "modification count". The iterator remembers it and checks it
            // on every next(). It changed -> the iterator refuses to continue ("fail-fast").
            System.out.println("Boom! ConcurrentModificationException");
        }

        System.out.println("--- Right #1: iterator.remove() ---");
        products = freshList();
        Iterator<String> iter = products.iterator();
        while (iter.hasNext()) {
            if (iter.next().startsWith("OUT:")) {
                iter.remove();            // removing THROUGH the iterator keeps it in sync
            }
        }
        System.out.println(products);

        System.out.println("--- Right #2: removeIf ---");
        products = freshList();
        // "p -> p.startsWith("OUT:")" is a lambda: "for each p, remove it if this is true".
        products.removeIf(p -> p.startsWith("OUT:"));
        System.out.println(products);

        System.out.println("--- Right #3: backwards index loop ---");
        products = freshList();
        // Going backwards means removing element i never shifts the elements we haven't visited yet.
        for (int i = products.size() - 1; i >= 0; i--) {
            if (products.get(i).startsWith("OUT:")) {
                products.remove(i);
            }
        }
        System.out.println(products);

        System.out.println("--- ListIterator: change while looping ---");
        products = freshList();
        ListIterator<String> li = products.listIterator();
        while (li.hasNext()) {
            li.set(li.next().toUpperCase());   // replace the element we just visited
        }
        System.out.println(products);
    }
}
