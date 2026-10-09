/*
 * Example01_VarAndTextBlocks.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   PART A - 'var' (Java 10): local variable type inference.
 *            Where you CAN and CANNOT use it, and when it helps.
 *   PART B - Text blocks (Java 15): multi-line strings with """ ... """
 *            for JSON, SQL and HTML - plus the \ (no newline) and \s escapes
 *            and String.formatted(...).
 *
 * HOW TO RUN:
 *   java Example01_VarAndTextBlocks.java
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Example01_VarAndTextBlocks {

    // var is NOT allowed for fields:   private var count = 0;   // compile error
    // var is NOT allowed for parameters or return types either.

    public static void main(String[] args) {

        // ================= PART A: var =================
        var shopName = "FreshMart";           // compiler infers String
        var itemCount = 42;                   // int
        var price = 199.99;                   // double
        var cart = new ArrayList<String>();   // ArrayList<String>
        cart.add("Milk");
        cart.add("Bread");

        System.out.println("A) " + shopName + " has " + itemCount + " items, price " + price + ", cart " + cart);

        // var shines when the type name is long and ALREADY visible on the right:
        Map<String, List<Integer>> marksOld = new HashMap<>();          // type written twice-ish
        var marksByStudent = new HashMap<String, List<Integer>>();      // shorter, still clear
        marksByStudent.put("Asha", List.of(90, 85));
        marksOld.putAll(marksByStudent);
        System.out.println("A) marks: " + marksOld);

        // var in for-each and classic for loops
        for (var entry : marksByStudent.entrySet()) {
            System.out.println("A) " + entry.getKey() + " -> " + entry.getValue());
        }
        for (var i = 1; i <= 3; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        // 'var' is still STATIC typing - the type is fixed at compile time:
        // shopName = 10;     // compile error: int cannot be converted to String

        // Things that do NOT compile with var:
        // var x;               // no initializer -> cannot infer
        // var y = null;        // null has no useful type
        // var nums = {1, 2};   // array initializer needs an explicit type
        // var f = () -> 1;     // lambda needs an explicit functional interface type

        // ================= PART B: text blocks =================
        // Old way: lots of \n, \" and + signs
        String jsonOld = "{\n" +
                "  \"name\": \"Laptop\",\n" +
                "  \"price\": 55000\n" +
                "}";

        // New way: what you see is what you get
        String json = """
                {
                  "name": "Laptop",
                  "price": 55000
                }""";
        System.out.println("B) Same JSON? " + json.equals(jsonOld));
        System.out.println(json);

        // SQL is much more readable - you'll see this in Spring @Query annotations
        String sql = """
                SELECT id, name, price
                FROM product
                WHERE price > 1000
                ORDER BY price DESC
                """;
        System.out.print(sql);   // ends with a newline because closing """ is on its own line

        // Indentation: the LEFT-most line (or the closing """) decides the margin.
        String html = """
                <ul>
                    <li>Java</li>
                    <li>Spring</li>
                </ul>
                """;
        System.out.print(html);

        // \ at the end of a line = "do not insert a newline here"
        String longLine = """
                Spring Boot makes it easy to create stand-alone, \
                production-grade applications.""";
        System.out.println(longLine);

        // \s = a space that is NOT stripped (trailing spaces are normally removed)
        String padded = """
                Name:\s\s\s
                """;
        System.out.println("[" + padded.strip().length() + " chars after strip, " + padded.length() + " before]");

        // formatted() - fill placeholders (like String.format)
        String email = """
                Dear %s,
                Your order #%d of Rs.%.2f has been shipped.
                """.formatted("Asha", 1042, 1499.5);
        System.out.print(email);
    }
}

/* Output:
A) FreshMart has 42 items, price 199.99, cart [Milk, Bread]
A) marks: {Asha=[90, 85]}
A) Asha -> [90, 85]
1 2 3
B) Same JSON? true
{
  "name": "Laptop",
  "price": 55000
}
SELECT id, name, price
FROM product
WHERE price > 1000
ORDER BY price DESC
<ul>
    <li>Java</li>
    <li>Spring</li>
</ul>
Spring Boot makes it easy to create stand-alone, production-grade applications.
[5 chars after strip, 9 before]
Dear Asha,
Your order #1042 of Rs.1499.50 has been shipped.
*/
