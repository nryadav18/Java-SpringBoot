/*
 * Example01_StringBasicsAndImmutability.java
 * ------------------------------------------------------------
 * What this file shows:
 *   1. Three ways to create a String.
 *   2. Strings are IMMUTABLE (they never change after creation).
 *   3. The String Pool (Java re-uses identical string literals).
 *   4. == (same object?) versus equals() (same text?).
 *
 * How to run (from this folder):
 *   java Example01_StringBasicsAndImmutability.java
 */
public class Example01_StringBasicsAndImmutability {

    public static void main(String[] args) {

        // ---------- 1. Creating strings ----------
        String city = "Hyderabad";                 // a string LITERAL -> stored in the String Pool
        String country = new String("India");      // 'new' ALWAYS makes a brand-new object on the heap
        char[] letters = {'J', 'a', 'v', 'a'};
        String language = new String(letters);     // built from a char array

        System.out.println("City: " + city);
        System.out.println("Country: " + country);
        System.out.println("Language: " + language);

        // ---------- 2. Immutability ----------
        // toUpperCase() does NOT change 'name'. It creates and RETURNS a new String.
        String name = "ravi";
        name.toUpperCase();                        // result thrown away -> nothing happens to 'name'
        System.out.println("After toUpperCase() without saving: " + name);

        name = name.toUpperCase();                 // save the NEW string back into the variable
        System.out.println("After saving the result: " + name);

        // concat() is the same story: a new String is returned.
        String greeting = "Hello";
        String bigger = greeting.concat(", World");
        System.out.println("greeting is still: " + greeting);
        System.out.println("bigger is: " + bigger);

        // ---------- 3. The String Pool ----------
        String a = "java";             // goes to the pool
        String b = "java";             // SAME literal -> Java re-uses the pooled object
        String c = new String("java"); // forced new object, NOT from the pool
        String d = c.intern();         // intern() = "give me the pooled copy of this text"

        // ---------- 4. == versus equals() ----------
        // == compares REFERENCES (are both arrows pointing at the very same object?)
        // equals() compares CONTENT (is the text identical, character by character?)
        System.out.println("a == b       : " + (a == b));        // true  (same pooled object)
        System.out.println("a == c       : " + (a == c));        // false (c is a different object)
        System.out.println("a.equals(c)  : " + a.equals(c));     // true  (same text)
        System.out.println("a == d       : " + (a == d));        // true  (intern() returned the pooled one)

        // equalsIgnoreCase() ignores capital/small letters
        System.out.println("\"JAVA\".equalsIgnoreCase(a): " + "JAVA".equalsIgnoreCase(a));

        // Safe comparison trick: put the known literal first so a null variable can't crash you.
        String userInput = null;
        System.out.println("\"yes\".equals(userInput): " + "yes".equals(userInput)); // false, no crash
        // userInput.equals("yes") would throw a NullPointerException!

        // compareTo() tells you dictionary ORDER (negative, zero, positive)
        System.out.println("\"apple\".compareTo(\"banana\"): " + "apple".compareTo("banana"));
        System.out.println("\"apple\".compareTo(\"apple\"): " + "apple".compareTo("apple"));
    }
}

/*
Output:
City: Hyderabad
Country: India
Language: Java
After toUpperCase() without saving: ravi
After saving the result: RAVI
greeting is still: Hello
bigger is: Hello, World
a == b       : true
a == c       : false
a.equals(c)  : true
a == d       : true
"JAVA".equalsIgnoreCase(a): true
"yes".equals(userInput): false
"apple".compareTo("banana"): -1
"apple".compareTo("apple"): 0
*/
