/*
 * Example04_StringBuilderPerformance.java
 * ------------------------------------------------------------
 * What this file shows:
 *   1. StringBuilder: a MUTABLE (changeable) string you can grow cheaply.
 *      append, insert, delete, deleteCharAt, replace, reverse, setCharAt, length, toString.
 *   2. WHY it matters: joining strings with + inside a loop creates thousands of
 *      throw-away objects. StringBuilder re-uses one buffer. We measure the time.
 *   3. StringBuffer: same methods, but thread-safe (synchronized) and therefore slower.
 *
 * How to run (from this folder):
 *   java Example04_StringBuilderPerformance.java
 * (The timing numbers will be different on your computer - that is normal.)
 */
public class Example04_StringBuilderPerformance {

    public static void main(String[] args) {

        // ---------- 1. StringBuilder basics ----------
        StringBuilder sb = new StringBuilder("Spring");
        sb.append(" Boot");                 // Spring Boot
        sb.append(' ').append(3);           // methods return the SAME builder -> you can chain
        System.out.println("append      : " + sb);

        sb.insert(0, "I love ");            // add text at position 0
        System.out.println("insert      : " + sb);

        sb.replace(0, 6, "We like");        // replace characters 0..5 with new text
        System.out.println("replace     : " + sb);

        sb.delete(0, 8);                    // remove characters 0..7
        System.out.println("delete      : " + sb);

        sb.deleteCharAt(sb.length() - 1);   // remove the last character ('3')
        System.out.println("deleteCharAt: [" + sb + "]");

        sb.setCharAt(0, 's');               // change one character
        System.out.println("setCharAt   : " + sb);

        System.out.println("length      : " + sb.length());
        System.out.println("reverse     : " + new StringBuilder("level up").reverse());

        // When you are done, turn it back into a normal (immutable) String:
        String finalText = sb.toString();
        System.out.println("toString    : " + finalText);

        // ---------- 2. Performance: + in a loop vs StringBuilder ----------
        int times = 20_000;

        long start = System.nanoTime();
        String slow = "";
        for (int i = 0; i < times; i++) {
            slow = slow + "x";   // EACH time: copy the whole old string + 1 char into a NEW object
        }
        long plusMillis = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        StringBuilder fast = new StringBuilder();
        for (int i = 0; i < times; i++) {
            fast.append("x");    // just adds 1 char to the existing buffer
        }
        String fastResult = fast.toString();
        long builderMillis = (System.nanoTime() - start) / 1_000_000;

        System.out.println();
        System.out.println("Both strings have length " + slow.length() + " and " + fastResult.length());
        System.out.println("Using +             took about " + plusMillis + " ms");
        System.out.println("Using StringBuilder took about " + builderMillis + " ms");
        System.out.println("StringBuilder was faster: " + (builderMillis <= plusMillis));

        // ---------- 3. StringBuffer ----------
        // Same API as StringBuilder. Every method is 'synchronized' (safe if many threads
        // share ONE buffer). That safety costs speed. In normal code use StringBuilder.
        StringBuffer buffer = new StringBuffer("Order");
        buffer.append("#").append(1001);
        System.out.println("StringBuffer: " + buffer);

        // ---------- Practical use: build a receipt line by line ----------
        String[] products = {"Milk", "Bread", "Eggs"};
        StringBuilder receipt = new StringBuilder();
        for (int i = 0; i < products.length; i++) {
            receipt.append(i + 1).append(". ").append(products[i]).append('\n');
        }
        System.out.print(receipt);
    }
}

/*
Output:
append      : Spring Boot 3
insert      : I love Spring Boot 3
replace     : We like Spring Boot 3
delete      : Spring Boot 3
deleteCharAt: [Spring Boot ]
setCharAt   : spring Boot 
length      : 12
reverse     : pu level
toString    : spring Boot 

Both strings have length 20000 and 20000
Using +             took about 232 ms
Using StringBuilder took about 1 ms     (your numbers will differ)
StringBuilder was faster: true
StringBuffer: Order#1001
1. Milk
2. Bread
3. Eggs
*/
