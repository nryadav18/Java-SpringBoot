/*
 * Example05_CharHandling.java
 * ------------------------------------------------------------
 * What this file shows:
 *   1. char vs String ('A' vs "A").
 *   2. chars are really numbers (Unicode codes) -> 'A' + 1 is 66.
 *   3. The Character helper class: isDigit, isLetter, isUpperCase, isWhitespace, toUpperCase...
 *   4. Looping over a String: charAt loop and toCharArray().
 *   5. Practical: a simple password strength checker.
 *
 * How to run (from this folder):
 *   java Example05_CharHandling.java
 */
public class Example05_CharHandling {

    public static void main(String[] args) {

        // ---------- 1. char vs String ----------
        char grade = 'A';          // single quotes -> ONE character (primitive type)
        String word = "A";         // double quotes -> a String object (0 or more characters)
        System.out.println("char: " + grade + ", String: " + word);

        // ---------- 2. chars are numbers ----------
        System.out.println("(int) 'A'       = " + (int) 'A');      // 65
        System.out.println("'A' + 1         = " + ('A' + 1));      // 66 (number math!)
        System.out.println("(char) ('A' + 1) = " + (char) ('A' + 1)); // B
        System.out.println("'7' - '0'       = " + ('7' - '0'));    // 7 -> classic trick: digit char to int

        // ---------- 3. Character helper methods ----------
        char[] samples = {'a', 'Z', '5', ' ', '@'};
        for (char ch : samples) {
            System.out.printf("'%c' letter=%b digit=%b upper=%b space=%b%n",
                    ch,
                    Character.isLetter(ch),
                    Character.isDigit(ch),
                    Character.isUpperCase(ch),
                    Character.isWhitespace(ch));
        }
        System.out.println("Character.toUpperCase('q') = " + Character.toUpperCase('q'));
        System.out.println("Character.getNumericValue('9') = " + Character.getNumericValue('9'));

        // ---------- 4. Looping over a String ----------
        String course = "Java";
        System.out.print("charAt loop     : ");
        for (int i = 0; i < course.length(); i++) {
            System.out.print(course.charAt(i) + " ");
        }
        System.out.println();

        System.out.print("toCharArray loop: ");
        for (char ch : course.toCharArray()) {
            System.out.print(ch + "-");
        }
        System.out.println();

        // char array back to String
        char[] chars = {'B', 'o', 'o', 't'};
        System.out.println("String.valueOf(chars): " + String.valueOf(chars));

        // ---------- 5. Practical: password strength ----------
        System.out.println();
        checkPassword("hello");
        checkPassword("Hello123");
        checkPassword("Hello@123");
    }

    /** Counts kinds of characters and prints a simple strength label. */
    static void checkPassword(String password) {
        int upper = 0, lower = 0, digits = 0, special = 0;
        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                upper++;
            } else if (Character.isLowerCase(ch)) {
                lower++;
            } else if (Character.isDigit(ch)) {
                digits++;
            } else {
                special++;
            }
        }
        boolean strong = password.length() >= 8 && upper > 0 && lower > 0 && digits > 0 && special > 0;
        System.out.printf("%-10s -> upper=%d lower=%d digits=%d special=%d -> %s%n",
                password, upper, lower, digits, special, strong ? "STRONG" : "weak");
    }
}

/*
Output:
char: A, String: A
(int) 'A'       = 65
'A' + 1         = 66
(char) ('A' + 1) = B
'7' - '0'       = 7
'a' letter=true digit=false upper=false space=false
'Z' letter=true digit=false upper=true space=false
'5' letter=false digit=true upper=false space=false
' ' letter=false digit=false upper=false space=true
'@' letter=false digit=false upper=false space=false
Character.toUpperCase('q') = Q
Character.getNumericValue('9') = 9
charAt loop     : J a v a 
toCharArray loop: J-a-v-a-
String.valueOf(chars): Boot

hello      -> upper=0 lower=5 digits=0 special=0 -> weak
Hello123   -> upper=1 lower=4 digits=3 special=0 -> weak
Hello@123  -> upper=1 lower=4 digits=3 special=1 -> STRONG
*/
