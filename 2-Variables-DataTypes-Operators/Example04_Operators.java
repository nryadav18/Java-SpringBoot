/*
 * ============================================================
 *  Topic 2 - Example 04 : Operators
 * ============================================================
 *  What this file demonstrates:
 *    - Arithmetic: + - * / %
 *    - Integer division and modulo (remainder) - and the classic traps.
 *    - Assignment shortcuts: += -= *= /= %=
 *    - Increment / decrement: ++ and -- (prefix vs postfix)
 *    - Relational (comparison): == != > < >= <=
 *    - Logical: && || ! (and short-circuit behaviour)
 *    - Operator precedence (which runs first).
 *
 *  How to run:
 *    java Example04_Operators.java
 * ============================================================
 */
public class Example04_Operators {

    public static void main(String[] args) {

        // ---------- 1. Arithmetic ----------
        int a = 17, b = 5;
        System.out.println("--- Arithmetic (a=17, b=5) ---");
        System.out.println("a + b = " + (a + b));   // 22
        System.out.println("a - b = " + (a - b));   // 12
        System.out.println("a * b = " + (a * b));   // 85
        System.out.println("a / b = " + (a / b));   // 3   <- INTEGER division: decimal part thrown away
        System.out.println("a % b = " + (a % b));   // 2   <- remainder (17 = 5*3 + 2)

        // To get a decimal result, at least ONE side must be a decimal.
        System.out.println("a / 5.0 = " + (a / 5.0));            // 3.4
        System.out.println("(double) a / b = " + ((double) a / b)); // 3.4 (cast BEFORE dividing)
        System.out.println("(double) (a / b) = " + ((double) (a / b))); // 3.0 (too late! division already done)

        // Modulo is super useful:
        int number = 14;
        System.out.println(number + " is even? " + (number % 2 == 0));   // even numbers have remainder 0
        int totalMinutes = 135;
        System.out.println(totalMinutes + " minutes = " + (totalMinutes / 60) + " h " + (totalMinutes % 60) + " min");
        int amount = 4728;
        System.out.println("Last digit of " + amount + " = " + (amount % 10));

        // ---------- 2. Assignment shortcuts ----------
        System.out.println("--- Assignment ---");
        int wallet = 100;
        wallet += 50;   // same as wallet = wallet + 50  -> 150
        wallet -= 30;   // wallet = wallet - 30          -> 120
        wallet *= 2;    // wallet = wallet * 2           -> 240
        wallet /= 4;    // wallet = wallet / 4           -> 60
        wallet %= 7;    // wallet = wallet % 7           -> 4
        System.out.println("wallet = " + wallet);

        // ---------- 3. Increment / decrement ----------
        System.out.println("--- ++ and -- ---");
        int count = 5;
        count++;                       // add 1 -> 6
        count--;                       // subtract 1 -> 5
        System.out.println("count = " + count);

        int x = 10;
        int y = x++;   // POSTFIX: use the OLD value first (y = 10), THEN increase x (x = 11)
        System.out.println("postfix: x = " + x + ", y = " + y);

        int p = 10;
        int q = ++p;   // PREFIX: increase FIRST (p = 11), THEN use it (q = 11)
        System.out.println("prefix : p = " + p + ", q = " + q);

        // ---------- 4. Relational (comparison) -> result is boolean ----------
        System.out.println("--- Relational ---");
        int age = 20;
        System.out.println("age == 20 : " + (age == 20));  // equal to (TWO equals signs!)
        System.out.println("age != 18 : " + (age != 18));  // not equal
        System.out.println("age > 18  : " + (age > 18));
        System.out.println("age < 18  : " + (age < 18));
        System.out.println("age >= 20 : " + (age >= 20));
        System.out.println("age <= 19 : " + (age <= 19));

        // ---------- 5. Logical ----------
        System.out.println("--- Logical ---");
        boolean hasTicket = true;
        boolean hasId = false;
        System.out.println("ticket AND id : " + (hasTicket && hasId));  // true only if BOTH true
        System.out.println("ticket OR id  : " + (hasTicket || hasId));  // true if AT LEAST one true
        System.out.println("NOT ticket    : " + (!hasTicket));          // flips true <-> false

        // Real use: can this person enter the movie?
        int viewerAge = 16;
        boolean withParent = true;
        boolean canWatch = viewerAge >= 18 || (viewerAge >= 13 && withParent);
        System.out.println("Can watch? " + canWatch);

        // Short-circuit: && stops as soon as the answer is known.
        int itemsInCart = 0;
        // If itemsInCart is 0, the right side (which would divide by zero!) is never evaluated.
        boolean bigAverage = itemsInCart > 0 && (500 / itemsInCart) > 100;
        System.out.println("Safe thanks to short-circuit: " + bigAverage);

        // ---------- 6. Precedence ----------
        System.out.println("--- Precedence ---");
        System.out.println("2 + 3 * 4     = " + (2 + 3 * 4));     // 14: * before +
        System.out.println("(2 + 3) * 4   = " + ((2 + 3) * 4));   // 20: brackets first
        System.out.println("10 - 4 - 3    = " + (10 - 4 - 3));    // 3: same level -> left to right
        System.out.println("20 / 2 * 5    = " + (20 / 2 * 5));    // 50: left to right, not 2
        System.out.println("true || false && false = " + (true || false && false)); // && before ||
    }
}

// Output:
// --- Arithmetic (a=17, b=5) ---
// a + b = 22
// a - b = 12
// a * b = 85
// a / b = 3
// a % b = 2
// a / 5.0 = 3.4
// (double) a / b = 3.4
// (double) (a / b) = 3.0
// 14 is even? true
// 135 minutes = 2 h 15 min
// Last digit of 4728 = 8
// --- Assignment ---
// wallet = 4
// --- ++ and -- ---
// count = 5
// postfix: x = 11, y = 10
// prefix : p = 11, q = 11
// --- Relational ---
// age == 20 : true
// age != 18 : true
// age > 18  : true
// age < 18  : false
// age >= 20 : true
// age <= 19 : false
// --- Logical ---
// ticket AND id : false
// ticket OR id  : true
// NOT ticket    : false
// Can watch? true
// Safe thanks to short-circuit: false
// --- Precedence ---
// 2 + 3 * 4     = 14
// (2 + 3) * 4   = 20
// 10 - 4 - 3    = 3
// 20 / 2 * 5    = 50
// true || false && false = true
