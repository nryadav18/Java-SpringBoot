/*
 * Example05_ToStringEqualsHashCode.java
 * ------------------------------------------------------------
 * What this file shows (domain: a Library):
 *   1. Printing an object without toString() -> "ClassName@1b6d3586" (not helpful).
 *   2. Writing our own toString() -> readable text.
 *   3. equals(): by default it behaves like == (same object?). We override it to
 *      compare by DATA (two books with the same ISBN are "equal").
 *   4. hashCode(): must be overridden together with equals() - otherwise HashSet/HashMap break.
 *
 * Every class automatically gets toString(), equals() and hashCode() from the class
 * java.lang.Object (the "parent of all classes" - you'll learn about this in Topic 7).
 * @Override tells the compiler "I am replacing the inherited version - please check I spelled it right".
 *
 * How to run (from this folder):
 *   java Example05_ToStringEqualsHashCode.java
 */
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Example05_ToStringEqualsHashCode {

    public static void main(String[] args) {

        // ---------- 1. No toString() ----------
        PlainBook plain = new PlainBook("978-0134685991", "Effective Java");
        String printed = plain.toString();
        // Looks like PlainBook@5ca881b5 - the part after @ changes every run.
        System.out.println("Without toString: " + printed.substring(0, printed.indexOf('@') + 1) + "<some hex number>");

        // ---------- 2. With toString() ----------
        Book b1 = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
        System.out.println("With toString   : " + b1);   // println calls toString() for us

        // ---------- 3. equals() ----------
        Book b2 = new Book("978-0134685991", "Effective Java", "Joshua Bloch");  // same data, new object
        System.out.println("b1 == b2      : " + (b1 == b2));        // false - different objects
        System.out.println("b1.equals(b2) : " + b1.equals(b2));     // true  - same ISBN (our rule)

        PlainBook plain2 = new PlainBook("978-0134685991", "Effective Java");
        System.out.println("plain.equals(plain2) (no override): " + plain.equals(plain2)); // false

        // ---------- 4. hashCode() ----------
        System.out.println("same hashCode : " + (b1.hashCode() == b2.hashCode()));  // true - required!

        // A HashSet keeps only UNIQUE items. It uses hashCode() + equals() to decide.
        Set<Book> shelf = new HashSet<>();
        shelf.add(b1);
        shelf.add(b2);   // considered a duplicate -> not added
        System.out.println("Books on shelf: " + shelf.size());

        Set<PlainBook> plainShelf = new HashSet<>();
        plainShelf.add(plain);
        plainShelf.add(plain2);   // without equals/hashCode both are kept
        System.out.println("Plain books on shelf: " + plainShelf.size());
    }
}

/** A class WITHOUT toString / equals / hashCode. */
class PlainBook {
    String isbn;
    String title;

    PlainBook(String isbn, String title) {
        this.isbn = isbn;
        this.title = title;
    }
}

/** A class WITH proper toString / equals / hashCode. Two books are equal if their ISBN is equal. */
class Book {
    String isbn;
    String title;
    String author;

    Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    @Override
    public String toString() {
        return "Book{isbn='" + isbn + "', title='" + title + "', author='" + author + "'}";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;                         // same object -> surely equal
        if (other == null || getClass() != other.getClass()) {  // null or a different class -> not equal
            return false;
        }
        Book that = (Book) other;                               // treat 'other' as a Book (casting: Topic 8)
        return Objects.equals(isbn, that.isbn);                 // our business rule: compare ISBN
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);                              // use the SAME fields as equals()
    }
}

/*
Output:
Without toString: PlainBook@<some hex number>
With toString   : Book{isbn='978-0134685991', title='Effective Java', author='Joshua Bloch'}
b1 == b2      : false
b1.equals(b2) : true
plain.equals(plain2) (no override): false
same hashCode : true
Books on shelf: 1
Plain books on shelf: 2
*/
