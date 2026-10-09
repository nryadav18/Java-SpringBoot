/*
 * Example09_HowHashMapWorks.java
 *
 * What this file demonstrates - how HashMap / HashSet find things so fast:
 *   1. Every key has a hashCode() (an int "fingerprint").
 *   2. HashMap turns the hash into a BUCKET number (index into an internal array).
 *   3. Inside a bucket, equals() picks the exact key.
 *   4. Two different keys can land in the same bucket = a COLLISION (that's fine).
 *   5. THE RULE: if you override equals(), you MUST override hashCode() too.
 *      Broken key class (equals only)   -> map.get() can't find your key, Set keeps "duplicates".
 *      Correct key class (both)          -> everything works.
 *   6. Never change a key's fields after putting it into a map.
 *
 * Domain: a library looking up books by ISBN.
 *
 * How to run:
 *   java Example09_HowHashMapWorks.java
 *
 * Output:
 *   --- Buckets (table size 16) ---
 *   "Asha"  hashCode=2050251  -> bucket 4
 *   "Ravi"  hashCode=2539842  -> bucket 4     <- same bucket as "Asha": a collision
 *   "Meena" hashCode=74220576 -> bucket 12
 *   "Aa" and "BB" have the SAME hashCode: 2112 and 2112 -> a collision, still both stored: {Aa=1, BB=2}
 *   --- Broken key (equals WITHOUT hashCode) ---
 *   equals? true   same hashCode? false
 *   get with an equal key: null      <- lost!
 *   Set size (should be 1): 2
 *   --- Correct key (equals AND hashCode) ---
 *   equals? true   same hashCode? true
 *   get with an equal key: Clean Code
 *   Set size (should be 1): 1
 *   --- Mutating a key after put ---
 *   Before change: Java Basics
 *   After change : null      <- the entry is stuck in the old bucket
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Example09_HowHashMapWorks {

    public static void main(String[] args) {
        System.out.println("--- Buckets (table size 16) ---");
        // A new HashMap starts with 16 buckets and grows (doubles) when it is 75% full.
        for (String key : new String[]{"Asha", "Ravi", "Meena"}) {
            int h = key.hashCode();
            System.out.printf("%-7s hashCode=%-8d -> bucket %d%n", "\"" + key + "\"", h, bucketFor(h, 16));
        }
        Map<String, Integer> collide = new HashMap<>();
        collide.put("Aa", 1);
        collide.put("BB", 2);
        System.out.println("\"Aa\" and \"BB\" have the SAME hashCode: " + "Aa".hashCode() + " and " + "BB".hashCode()
                + " -> a collision, still both stored: " + collide);

        System.out.println("--- Broken key (equals WITHOUT hashCode) ---");
        Map<BrokenIsbn, String> brokenMap = new HashMap<>();
        brokenMap.put(new BrokenIsbn("978-0132350884"), "Clean Code");
        BrokenIsbn sameIsbn = new BrokenIsbn("978-0132350884");
        System.out.println("equals? " + sameIsbn.equals(new BrokenIsbn("978-0132350884"))
                + "   same hashCode? " + (sameIsbn.hashCode() == new BrokenIsbn("978-0132350884").hashCode()));
        System.out.println("get with an equal key: " + brokenMap.get(sameIsbn) + "      <- lost!");
        Set<BrokenIsbn> brokenSet = new HashSet<>();
        brokenSet.add(new BrokenIsbn("111"));
        brokenSet.add(new BrokenIsbn("111"));
        System.out.println("Set size (should be 1): " + brokenSet.size());

        System.out.println("--- Correct key (equals AND hashCode) ---");
        Map<Isbn, String> goodMap = new HashMap<>();
        goodMap.put(new Isbn("978-0132350884"), "Clean Code");
        Isbn lookup = new Isbn("978-0132350884");
        System.out.println("equals? " + lookup.equals(new Isbn("978-0132350884"))
                + "   same hashCode? " + (lookup.hashCode() == new Isbn("978-0132350884").hashCode()));
        System.out.println("get with an equal key: " + goodMap.get(lookup));
        Set<Isbn> goodSet = new HashSet<>();
        goodSet.add(new Isbn("111"));
        goodSet.add(new Isbn("111"));
        System.out.println("Set size (should be 1): " + goodSet.size());

        System.out.println("--- Mutating a key after put ---");
        MutableIsbn key = new MutableIsbn("222");
        Map<MutableIsbn, String> shelf = new HashMap<>();
        shelf.put(key, "Java Basics");
        System.out.println("Before change: " + shelf.get(key));
        key.value = "333";                     // its hashCode changes -> HashMap looks in the WRONG bucket
        System.out.println("After change : " + shelf.get(key) + "      <- the entry is stuck in the old bucket");
    }

    // Simplified version of what HashMap does: mix the high bits in, then keep the low bits.
    static int bucketFor(int hash, int tableSize) {
        int spread = hash ^ (hash >>> 16);
        return spread & (tableSize - 1);       // same as "spread % tableSize" for powers of 2 (and never negative)
    }
}

// WRONG: equals() overridden, hashCode() NOT. Two "equal" objects get different hash codes
// (the default hashCode is based on the object's identity), so they land in different buckets.
class BrokenIsbn {
    private final String value;

    BrokenIsbn(String value) { this.value = value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BrokenIsbn)) return false;
        return value.equals(((BrokenIsbn) o).value);
    }
    // hashCode() missing!
}

// RIGHT: equals() and hashCode() use the SAME fields.
final class Isbn {
    private final String value;   // final -> can't change after creation -> safe as a key

    Isbn(String value) { this.value = value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Isbn)) return false;
        return value.equals(((Isbn) o).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);   // equal objects -> equal hash codes. Guaranteed.
    }
}

// DANGEROUS: a key whose fields can change.
class MutableIsbn {
    String value;

    MutableIsbn(String value) { this.value = value; }

    @Override
    public boolean equals(Object o) {
        return o instanceof MutableIsbn && value.equals(((MutableIsbn) o).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
