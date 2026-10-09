/*
 * Example03_SetTypes.java
 *
 * What this file demonstrates:
 *   - The Set interface: NO DUPLICATES, no index (no get(i)).
 *   - HashSet       : fastest, NO guaranteed order
 *   - LinkedHashSet : keeps INSERTION order
 *   - TreeSet       : keeps SORTED order (+ handy methods: first, last, headSet, ceiling)
 *   - add() returns false for a duplicate
 *   - Typical uses: remove duplicates from a list, "have I seen this before?",
 *     set maths (union, intersection, difference).
 *
 * Domain: tags on food-delivery orders and student course enrolments.
 *
 * How to run:
 *   java Example03_SetTypes.java
 *
 * Output (HashSet order is decided by hash codes - it may look "random", that is expected):
 *   HashSet       : [dessert, veg, combo, spicy]
 *   LinkedHashSet : [veg, spicy, combo, dessert]
 *   TreeSet       : [combo, dessert, spicy, veg]
 *   add("veg") again returned: false  size still 4
 *   Unique cities: [Pune, Delhi, Chennai]
 *   TreeSet of prices: [49, 99, 149, 199, 299]
 *   first=49 last=299 cheaper than 150: [49, 99, 149] cheapest >= 120: 149
 *   Asha's courses: [Java, SQL, Spring]
 *   Ravi's courses: [Java, Docker, AWS]
 *   Union        : [Java, SQL, Spring, Docker, AWS]
 *   Intersection : [Java]
 *   Only Asha    : [SQL, Spring]
 */
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Example03_SetTypes {

    public static void main(String[] args) {
        String[] tags = {"veg", "spicy", "combo", "veg", "dessert", "spicy"};

        Set<String> hashSet = new HashSet<>();
        Set<String> linkedHashSet = new LinkedHashSet<>();
        Set<String> treeSet = new TreeSet<>();
        for (String tag : tags) {
            hashSet.add(tag);
            linkedHashSet.add(tag);
            treeSet.add(tag);
        }
        System.out.println("HashSet       : " + hashSet);         // no order promise
        System.out.println("LinkedHashSet : " + linkedHashSet);   // order of first insertion
        System.out.println("TreeSet       : " + treeSet);         // alphabetical

        boolean added = hashSet.add("veg");
        System.out.println("add(\"veg\") again returned: " + added + "  size still " + hashSet.size());

        // Remove duplicates from a List but keep the original order:
        List<String> cities = List.of("Pune", "Delhi", "Pune", "Chennai", "Delhi");
        Set<String> uniqueCities = new LinkedHashSet<>(cities);   // constructor copies all elements
        System.out.println("Unique cities: " + uniqueCities);

        // TreeSet has extra "navigation" methods because it is sorted
        TreeSet<Integer> prices = new TreeSet<>(List.of(199, 49, 299, 99, 149));
        System.out.println("TreeSet of prices: " + prices);
        System.out.println("first=" + prices.first() + " last=" + prices.last()
                + " cheaper than 150: " + prices.headSet(150)
                + " cheapest >= 120: " + prices.ceiling(120));

        // Set maths - always work on COPIES so the originals stay unchanged
        Set<String> asha = new LinkedHashSet<>(List.of("Java", "SQL", "Spring"));
        Set<String> ravi = new LinkedHashSet<>(List.of("Java", "Docker", "AWS"));
        System.out.println("Asha's courses: " + asha);
        System.out.println("Ravi's courses: " + ravi);

        Set<String> union = new LinkedHashSet<>(asha);
        union.addAll(ravi);                 // everything from both
        System.out.println("Union        : " + union);

        Set<String> common = new LinkedHashSet<>(asha);
        common.retainAll(ravi);             // keep only what is also in ravi
        System.out.println("Intersection : " + common);

        Set<String> onlyAsha = new LinkedHashSet<>(asha);
        onlyAsha.removeAll(ravi);           // remove everything ravi has
        System.out.println("Only Asha    : " + onlyAsha);
    }
}
