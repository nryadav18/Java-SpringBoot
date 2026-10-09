/*
 * Example06_InterviewStringPrograms.java
 * ------------------------------------------------------------
 * What this file shows: the classic string programs asked in almost every
 * Java fresher / junior interview, each in its own small method:
 *   1. Reverse a string (two ways)
 *   2. Palindrome check
 *   3. Count vowels and consonants
 *   4. Anagram check (two ways)
 *   5. Count words in a sentence
 *   6. First non-repeating character
 *   7. Reverse each word in a sentence
 *
 * How to run (from this folder):
 *   java Example06_InterviewStringPrograms.java
 */
import java.util.Arrays;

public class Example06_InterviewStringPrograms {

    public static void main(String[] args) {
        System.out.println("1. reverse(\"Spring\")          = " + reverseWithLoop("Spring"));
        System.out.println("   reverseWithBuilder(\"Spring\") = " + reverseWithBuilder("Spring"));

        System.out.println("2. isPalindrome(\"Madam\")      = " + isPalindrome("Madam"));
        System.out.println("   isPalindrome(\"Java\")       = " + isPalindrome("Java"));

        countVowelsAndConsonants("Hello World");

        System.out.println("4. isAnagram(\"listen\",\"silent\") = " + isAnagramBySorting("listen", "silent"));
        System.out.println("   isAnagram(\"Dormitory\",\"Dirty room\") = " + isAnagramByCounting("Dormitory", "Dirty room"));
        System.out.println("   isAnagram(\"hello\",\"world\")  = " + isAnagramBySorting("hello", "world"));

        System.out.println("5. countWords(\"  Java is   fun  \") = " + countWords("  Java is   fun  "));

        System.out.println("6. firstNonRepeating(\"swiss\") = " + firstNonRepeating("swiss"));

        System.out.println("7. reverseEachWord(\"I love Java\") = " + reverseEachWord("I love Java"));
    }

    // 1a. Reverse by walking from the last character to the first.
    static String reverseWithLoop(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = text.length() - 1; i >= 0; i--) {
            result.append(text.charAt(i));
        }
        return result.toString();
    }

    // 1b. Reverse using the built-in StringBuilder.reverse().
    static String reverseWithBuilder(String text) {
        return new StringBuilder(text).reverse().toString();
    }

    // 2. A palindrome reads the same forwards and backwards (ignoring case).
    //    Two-pointer technique: compare first with last, move inwards.
    static boolean isPalindrome(String text) {
        String s = text.toLowerCase();
        int left = 0;
        int right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;          // one mismatch is enough to say "no"
            }
            left++;
            right--;
        }
        return true;
    }

    // 3. Vowels are a, e, i, o, u. Consonants are the other LETTERS (spaces are ignored).
    static void countVowelsAndConsonants(String text) {
        int vowels = 0;
        int consonants = 0;
        for (char ch : text.toLowerCase().toCharArray()) {
            if ("aeiou".indexOf(ch) != -1) {       // is ch one of "aeiou"?
                vowels++;
            } else if (Character.isLetter(ch)) {
                consonants++;
            }
        }
        System.out.println("3. \"" + text + "\" -> vowels=" + vowels + ", consonants=" + consonants);
    }

    // 4a. Anagram = same letters, same counts, different order. Sort both and compare.
    static boolean isAnagramBySorting(String first, String second) {
        char[] a = first.replace(" ", "").toLowerCase().toCharArray();
        char[] b = second.replace(" ", "").toLowerCase().toCharArray();
        if (a.length != b.length) {
            return false;
        }
        Arrays.sort(a);
        Arrays.sort(b);
        return Arrays.equals(a, b);
    }

    // 4b. Faster idea: count each letter. +1 for the first word, -1 for the second.
    //     If every counter ends at 0, the words are anagrams.
    static boolean isAnagramByCounting(String first, String second) {
        String a = first.replace(" ", "").toLowerCase();
        String b = second.replace(" ", "").toLowerCase();
        if (a.length() != b.length()) {
            return false;
        }
        int[] counts = new int[26];                // one box per letter a..z
        for (int i = 0; i < a.length(); i++) {
            counts[a.charAt(i) - 'a']++;           // 'c' - 'a' = 2 -> box number 2
            counts[b.charAt(i) - 'a']--;
        }
        for (int c : counts) {
            if (c != 0) {
                return false;
            }
        }
        return true;
    }

    // 5. strip() removes outer spaces, "\\s+" means "one or more whitespace characters".
    static int countWords(String sentence) {
        String trimmed = sentence.strip();
        if (trimmed.isEmpty()) {
            return 0;
        }
        return trimmed.split("\\s+").length;
    }

    // 6. A character whose first and last position are the same appears only once.
    static char firstNonRepeating(String text) {
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (text.indexOf(ch) == text.lastIndexOf(ch)) {
                return ch;
            }
        }
        return '_';   // nothing found
    }

    // 7. Split into words, reverse each one, join them back with spaces.
    static String reverseEachWord(String sentence) {
        String[] words = sentence.split(" ");
        StringBuilder result = new StringBuilder();
        for (String w : words) {
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(new StringBuilder(w).reverse());
        }
        return result.toString();
    }
}

/*
Output:
1. reverse("Spring")          = gnirpS
   reverseWithBuilder("Spring") = gnirpS
2. isPalindrome("Madam")      = true
   isPalindrome("Java")       = false
3. "Hello World" -> vowels=3, consonants=7
4. isAnagram("listen","silent") = true
   isAnagram("Dormitory","Dirty room") = true
   isAnagram("hello","world")  = false
5. countWords("  Java is   fun  ") = 3
6. firstNonRepeating("swiss") = w
7. reverseEachWord("I love Java") = I evol avaJ
*/
