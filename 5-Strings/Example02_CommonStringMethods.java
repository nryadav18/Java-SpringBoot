/*
 * Example02_CommonStringMethods.java
 * ------------------------------------------------------------
 * What this file shows: the String methods you will use every single day.
 *   length, charAt, substring, indexOf, lastIndexOf, contains, startsWith, endsWith,
 *   replace, split, trim, strip, toUpperCase, toLowerCase, isEmpty, isBlank,
 *   String.join, repeat, valueOf.
 *
 * Remember: indexes start at 0 (the first character is at position 0).
 *
 * How to run (from this folder):
 *   java Example02_CommonStringMethods.java
 */
import java.util.Arrays;

public class Example02_CommonStringMethods {

    public static void main(String[] args) {

        String email = "priya.sharma@shop.com";
        //              0123456789...  (positions start at 0)

        // ---- length and charAt ----
        System.out.println("length()          : " + email.length());       // 21 characters
        System.out.println("charAt(0)         : " + email.charAt(0));      // 'p'
        System.out.println("last char         : " + email.charAt(email.length() - 1)); // 'm'

        // ---- searching ----
        int at = email.indexOf('@');                                       // position of '@'
        System.out.println("indexOf('@')      : " + at);                   // 12
        System.out.println("indexOf(\"xyz\")    : " + email.indexOf("xyz")); // -1 means "not found"
        System.out.println("lastIndexOf('.')  : " + email.lastIndexOf('.'));
        System.out.println("contains(\"shop\")  : " + email.contains("shop"));
        System.out.println("startsWith(\"priya\"): " + email.startsWith("priya"));
        System.out.println("endsWith(\".com\")  : " + email.endsWith(".com"));

        // ---- substring(start, end) : start is INCLUDED, end is EXCLUDED ----
        String userName = email.substring(0, at);       // from 0 up to (not including) '@'
        String domain = email.substring(at + 1);        // from after '@' to the end
        System.out.println("user name         : " + userName);
        System.out.println("domain            : " + domain);

        // ---- replace ----
        String phone = "98-765-43210";
        System.out.println("replace(\"-\", \"\") : " + phone.replace("-", ""));

        // ---- split : cut a string into pieces -> String[] ----
        String csvLine = "Laptop,55000,Electronics";
        String[] parts = csvLine.split(",");
        System.out.println("split(\",\")        : " + Arrays.toString(parts));
        System.out.println("product name      : " + parts[0]);
        System.out.println("price as int      : " + Integer.parseInt(parts[1])); // text -> number

        // ---- trim vs strip : remove spaces at both ends ----
        String messy = "   hello world   ";
        System.out.println("trim()            : [" + messy.trim() + "]");
        System.out.println("strip()           : [" + messy.strip() + "]");   // Java 11+, Unicode aware
        System.out.println("stripLeading()    : [" + messy.stripLeading() + "]");
        System.out.println("stripTrailing()   : [" + messy.stripTrailing() + "]");

        // ---- changing case ----
        System.out.println("toUpperCase()     : " + "Spring Boot".toUpperCase());
        System.out.println("toLowerCase()     : " + "Spring Boot".toLowerCase());

        // ---- isEmpty vs isBlank ----
        String empty = "";
        String spaces = "    ";
        System.out.println("\"\".isEmpty()      : " + empty.isEmpty());     // true  (length is 0)
        System.out.println("\"    \".isEmpty()  : " + spaces.isEmpty());    // false (length is 4)
        System.out.println("\"    \".isBlank()  : " + spaces.isBlank());    // true  (only whitespace)

        // ---- join and repeat ----
        String joined = String.join(" | ", "Home", "Products", "Cart");
        System.out.println("String.join       : " + joined);
        System.out.println("repeat(3)         : " + "ab".repeat(3));
        System.out.println("divider           : " + "-".repeat(20));

        // ---- converting other things to String ----
        int qty = 5;
        String qtyText = String.valueOf(qty);      // 5 -> "5"
        System.out.println("valueOf(5) + 1    : " + qtyText + 1);   // "51" (text joining, not math!)
        System.out.println("qty + 1           : " + (qty + 1));     // 6   (math because of brackets)

        // ---- chaining methods (each returns a NEW string) ----
        String raw = "  ORDER-1001  ";
        String clean = raw.strip().toLowerCase().replace("order-", "#");
        System.out.println("chained result    : " + clean);
    }
}

/*
Output:
length()          : 21
charAt(0)         : p
last char         : m
indexOf('@')      : 12
indexOf("xyz")    : -1
lastIndexOf('.')  : 17
contains("shop")  : true
startsWith("priya"): true
endsWith(".com")  : true
user name         : priya.sharma
domain            : shop.com
replace("-", "") : 9876543210
split(",")        : [Laptop, 55000, Electronics]
product name      : Laptop
price as int      : 55000
trim()            : [hello world]
strip()           : [hello world]
stripLeading()    : [hello world   ]
stripTrailing()   : [   hello world]
toUpperCase()     : SPRING BOOT
toLowerCase()     : spring boot
"".isEmpty()      : true
"    ".isEmpty()  : false
"    ".isBlank()  : true
String.join       : Home | Products | Cart
repeat(3)         : ababab
divider           : --------------------
valueOf(5) + 1    : 51
qty + 1           : 6
chained result    : #1001
*/
