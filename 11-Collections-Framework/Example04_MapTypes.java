/*
 * Example04_MapTypes.java
 *
 * What this file demonstrates:
 *   - The Map interface: KEY -> VALUE pairs. Keys are unique; values can repeat.
 *     (Map is part of the Collections Framework but does NOT extend Collection.)
 *   - put, get, getOrDefault, containsKey, remove, size, putIfAbsent
 *   - put() with an existing key REPLACES the old value
 *   - Looping over keySet(), values() and entrySet()
 *   - HashMap (no order) vs LinkedHashMap (insertion order) vs TreeMap (sorted by key)
 *   - Classic task: counting words / items with a Map
 *
 * Domain: a restaurant menu and order counting.
 *
 * How to run:
 *   java Example04_MapTypes.java
 *
 * Output (HashMap order may differ - that's expected):
 *   Menu: {Dosa=60, Idli=40, Vada=30}
 *   Price of Dosa: 60
 *   Price of Pizza: null
 *   Price of Pizza (default): 0
 *   Has Idli? true   Has 99 as a key? false   Has value 30? true
 *   put("Dosa", 70) returned old value: 60 -> {Dosa=70, Idli=40, Vada=30}
 *   putIfAbsent("Idli", 999) kept: 40
 *   After remove("Vada"): {Dosa=70, Idli=40}
 *   --- Looping ---
 *   key: Dosa
 *   key: Idli
 *   value: 70
 *   value: 40
 *   Dosa costs 70
 *   Idli costs 40
 *   --- 3 kinds of Map with the same data ---
 *   HashMap       : {banana=2, cherry=7, apple=5}
 *   LinkedHashMap : {cherry=7, apple=5, banana=2}
 *   TreeMap       : {apple=5, banana=2, cherry=7}
 *   --- Counting orders ---
 *   Order counts: {Dosa=3, Idli=2, Vada=1}
 *   merge() version: {Dosa=3, Idli=2, Vada=1}
 */
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class Example04_MapTypes {

    public static void main(String[] args) {
        // Map<KeyType, ValueType>
        Map<String, Integer> menu = new LinkedHashMap<>();   // LinkedHashMap -> predictable printing order
        menu.put("Dosa", 60);
        menu.put("Idli", 40);
        menu.put("Vada", 30);
        System.out.println("Menu: " + menu);

        System.out.println("Price of Dosa: " + menu.get("Dosa"));
        System.out.println("Price of Pizza: " + menu.get("Pizza"));                 // missing key -> null
        System.out.println("Price of Pizza (default): " + menu.getOrDefault("Pizza", 0));
        System.out.println("Has Idli? " + menu.containsKey("Idli")
                + "   Has 99 as a key? " + menu.containsKey("99")
                + "   Has value 30? " + menu.containsValue(30));

        Integer old = menu.put("Dosa", 70);   // same key -> value is REPLACED, old value returned
        System.out.println("put(\"Dosa\", 70) returned old value: " + old + " -> " + menu);

        menu.putIfAbsent("Idli", 999);        // only puts if the key is NOT there yet
        System.out.println("putIfAbsent(\"Idli\", 999) kept: " + menu.get("Idli"));

        menu.remove("Vada");
        System.out.println("After remove(\"Vada\"): " + menu);

        System.out.println("--- Looping ---");
        for (String dish : menu.keySet()) {          // only the keys
            System.out.println("key: " + dish);
        }
        for (int price : menu.values()) {             // only the values
            System.out.println("value: " + price);
        }
        // entrySet() gives key AND value together - the most efficient way to see both.
        // Map.Entry is a static nested interface (remember Topic 9!).
        for (Map.Entry<String, Integer> entry : menu.entrySet()) {
            System.out.println(entry.getKey() + " costs " + entry.getValue());
        }

        System.out.println("--- 3 kinds of Map with the same data ---");
        Map<String, Integer> hashMap = new HashMap<>();
        Map<String, Integer> linkedMap = new LinkedHashMap<>();
        Map<String, Integer> treeMap = new TreeMap<>();
        String[] fruits = {"cherry", "apple", "banana"};
        int[] stock = {7, 5, 2};
        for (int i = 0; i < fruits.length; i++) {
            hashMap.put(fruits[i], stock[i]);
            linkedMap.put(fruits[i], stock[i]);
            treeMap.put(fruits[i], stock[i]);
        }
        System.out.println("HashMap       : " + hashMap);     // order depends on hash codes
        System.out.println("LinkedHashMap : " + linkedMap);   // insertion order
        System.out.println("TreeMap       : " + treeMap);     // sorted by key

        System.out.println("--- Counting orders ---");
        String[] orders = {"Dosa", "Idli", "Dosa", "Vada", "Idli", "Dosa"};
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String item : orders) {
            int current = counts.getOrDefault(item, 0);   // 0 if we've never seen it
            counts.put(item, current + 1);
        }
        System.out.println("Order counts: " + counts);

        // Same thing in one line per item with merge(). Integer::sum is a "method reference"
        // (Topic 13) meaning "add the old value and 1 together".
        Map<String, Integer> counts2 = new LinkedHashMap<>();
        for (String item : orders) {
            counts2.merge(item, 1, Integer::sum);
        }
        System.out.println("merge() version: " + counts2);
    }
}
