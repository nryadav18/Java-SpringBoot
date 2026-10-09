/*
 * Example02_ListArrayListVsLinkedList.java
 *
 * What this file demonstrates:
 *   - The List interface: ORDERED, allows DUPLICATES, access by INDEX.
 *   - The most useful List methods: add, get, set, remove(index) vs remove(object),
 *     contains, indexOf, size, isEmpty, addAll, subList, clear, 3 ways to loop.
 *   - ArrayList (array inside) vs LinkedList (chain of nodes inside):
 *     same methods, different speed. A small timing demo makes it visible.
 *
 * Domain: a college class register.
 *
 * How to run:
 *   java Example02_ListArrayListVsLinkedList.java
 *
 * Output (the timing numbers will differ on your machine - look at which is FASTER):
 *   Students: [Asha, Ravi, Meena, Ravi]
 *   Duplicates allowed -> Ravi appears twice
 *   First: Asha, last: Ravi
 *   After set(1, "Kiran"): [Asha, Kiran, Meena, Ravi]
 *   remove(0) removed: Asha
 *   remove("Ravi") removed? true -> [Kiran, Meena]
 *   After addAll: [Kiran, Meena, Zoya, Arjun, Dev]
 *   subList(1, 3): [Meena, Zoya]
 *   Loop with index : 0=Kiran 1=Meena 2=Zoya 3=Arjun 4=Dev
 *   Loop with for-each: Kiran Meena Zoya Arjun Dev
 *   Integer list before: [10, 20, 30]
 *   remove(1) -> removes INDEX 1: [10, 30]
 *   remove(Integer.valueOf(10)) -> removes VALUE 10: [30]
 *   --- Speed test: 20,000 inserts at the FRONT ---
 *   ArrayList : 57 ms
 *   LinkedList: 4 ms
 *   --- Speed test: 20,000 reads by index (get(i)) ---
 *   ArrayList : 2 ms
 *   LinkedList: 344 ms
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Example02_ListArrayListVsLinkedList {

    public static void main(String[] args) {
        List<String> students = new ArrayList<>();
        students.add("Asha");
        students.add("Ravi");
        students.add("Meena");
        students.add("Ravi");                       // duplicates are fine in a List
        System.out.println("Students: " + students);
        System.out.println("Duplicates allowed -> Ravi appears twice");
        System.out.println("First: " + students.get(0) + ", last: " + students.get(students.size() - 1));

        students.set(1, "Kiran");                   // replace the element at index 1
        System.out.println("After set(1, \"Kiran\"): " + students);

        String removed = students.remove(0);        // remove by INDEX -> returns the removed element
        System.out.println("remove(0) removed: " + removed);
        boolean ok = students.remove("Ravi");       // remove by OBJECT -> returns true/false
        System.out.println("remove(\"Ravi\") removed? " + ok + " -> " + students);

        students.addAll(List.of("Zoya", "Arjun", "Dev"));   // add many at once
        System.out.println("After addAll: " + students);
        System.out.println("subList(1, 3): " + students.subList(1, 3));   // from index 1 up to (not including) 3

        // Looping - style 1: classic for with index (when you need the index)
        StringBuilder sb = new StringBuilder("Loop with index :");
        for (int i = 0; i < students.size(); i++) {
            sb.append(" ").append(i).append("=").append(students.get(i));
        }
        System.out.println(sb);

        // Looping - style 2: for-each (the normal choice)
        System.out.print("Loop with for-each:");
        for (String s : students) {
            System.out.print(" " + s);
        }
        System.out.println();
        // (Style 3, the Iterator, is in Example06.)

        // A classic trap with List<Integer>: remove(int index) vs remove(Object value)
        List<Integer> marks = new ArrayList<>(List.of(10, 20, 30));
        System.out.println("Integer list before: " + marks);
        marks.remove(1);                            // int argument -> treated as an INDEX
        System.out.println("remove(1) -> removes INDEX 1: " + marks);
        marks.remove(Integer.valueOf(10));          // Integer object -> treated as a VALUE
        System.out.println("remove(Integer.valueOf(10)) -> removes VALUE 10: " + marks);

        speedTest();
    }

    static void speedTest() {
        int n = 20_000;
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();

        System.out.println("--- Speed test: 20,000 inserts at the FRONT ---");
        // ArrayList must SHIFT every existing element one step right on each insert at index 0.
        // LinkedList just links a new node at the start.
        System.out.println("ArrayList : " + timeInsertFront(arrayList, n) + " ms");
        System.out.println("LinkedList: " + timeInsertFront(linkedList, n) + " ms");

        System.out.println("--- Speed test: 20,000 reads by index (get(i)) ---");
        // ArrayList jumps straight to position i (it's an array inside).
        // LinkedList must WALK node by node from the start (or end) to reach position i.
        System.out.println("ArrayList : " + timeGetByIndex(arrayList) + " ms");
        System.out.println("LinkedList: " + timeGetByIndex(linkedList) + " ms");
    }

    static long timeInsertFront(List<Integer> list, int n) {
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            list.add(0, i);
        }
        return (System.nanoTime() - start) / 1_000_000;
    }

    static long timeGetByIndex(List<Integer> list) {
        long start = System.nanoTime();
        long sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }
        if (sum < 0) {
            System.out.println("never");   // uses "sum" so the JVM cannot skip the loop
        }
        return (System.nanoTime() - start) / 1_000_000;
    }
}
