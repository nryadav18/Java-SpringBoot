/*
 * Example03_FormattingAndTextBlocks.java
 * ------------------------------------------------------------
 * What this file shows:
 *   1. String.format(...) and "...".formatted(...) with placeholders (%s, %d, %.2f, %n ...).
 *   2. Width and alignment to print neat tables (a shop bill).
 *   3. Text blocks (""" ... """, Java 15+) for multi-line text like JSON, SQL, HTML.
 *
 * How to run (from this folder):
 *   java Example03_FormattingAndTextBlocks.java
 */
public class Example03_FormattingAndTextBlocks {

    public static void main(String[] args) {

        // ---------- 1. Placeholders ----------
        // %s = any value as text, %d = whole number, %f = decimal number, %n = new line
        String customer = "Anita";
        int items = 3;
        double total = 1249.5;

        String message = String.format("Hi %s, you bought %d items. Total: Rs. %.2f", customer, items, total);
        System.out.println(message);

        // .formatted() (Java 15+) does the same thing, called on the template itself
        String sameMessage = "Hi %s, you bought %d items. Total: Rs. %.2f".formatted(customer, items, total);
        System.out.println(sameMessage);

        // printf = format + print in one go (no new line unless you add %n)
        System.out.printf("Discount: %d%%%n", 10);      // %% prints a real percent sign

        // ---------- 2. Width and alignment -> neat tables ----------
        // %-12s = text, 12 characters wide, LEFT aligned (because of the minus)
        // %8.2f = number, 8 characters wide, RIGHT aligned, 2 decimals
        // %05d  = whole number, 5 wide, padded with zeros
        System.out.println();
        System.out.println("----- SHOP BILL -----");
        System.out.printf("%-12s %3s %8s%n", "Item", "Qty", "Price");
        System.out.printf("%-12s %3d %8.2f%n", "Notebook", 2, 120.0);
        System.out.printf("%-12s %3d %8.2f%n", "Pen", 5, 50.5);
        System.out.printf("%-12s %3d %8.2f%n", "Backpack", 1, 899.0);
        System.out.printf("Invoice no: %05d%n", 42);
        System.out.printf("Big number with commas: %,d%n", 1500000);

        // ---------- 3. Text blocks ----------
        // Starts with """ followed by a NEW LINE. Ends with """.
        // Java removes the common indentation for you. No need for \n or + everywhere.
        String json = """
                {
                  "name": "Laptop",
                  "price": 55000,
                  "inStock": true
                }
                """;
        System.out.println();
        System.out.println("JSON text block:");
        System.out.print(json);   // text block already ends with a new line

        // Text blocks + formatted() = templates
        String sql = """
                SELECT id, name, price
                FROM product
                WHERE price < %d
                ORDER BY price""".formatted(1000);
        System.out.println("SQL text block:");
        System.out.println(sql);

        // The old way, for comparison - harder to read and easy to break:
        String oldWay = "{\n" +
                "  \"name\": \"Laptop\"\n" +
                "}";
        System.out.println("Old way:");
        System.out.println(oldWay);
    }
}

/*
Output:
Hi Anita, you bought 3 items. Total: Rs. 1249.50
Hi Anita, you bought 3 items. Total: Rs. 1249.50
Discount: 10%

----- SHOP BILL -----
Item         Qty    Price
Notebook       2   120.00
Pen            5    50.50
Backpack       1   899.00
Invoice no: 00042
Big number with commas: 1,500,000

JSON text block:
{
  "name": "Laptop",
  "price": 55000,
  "inStock": true
}
SQL text block:
SELECT id, name, price
FROM product
WHERE price < 1000
ORDER BY price
Old way:
{
  "name": "Laptop"
}
*/
