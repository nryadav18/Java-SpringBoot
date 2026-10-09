/*
 * Example04_TryWithResources.java
 *
 * What this file demonstrates:
 *   - The problem: resources (files, database connections, network sockets) MUST be closed,
 *     even when something fails. The old way needs a long finally block.
 *   - try-with-resources:  try (Resource r = new Resource()) { ... }
 *       Java closes r AUTOMATICALLY at the end - success or failure.
 *   - Any class that implements AutoCloseable can be used this way.
 *   - Several resources are closed in REVERSE order of opening.
 *   - "Suppressed" exceptions: if both the body and close() fail, you still see both.
 *   - A real JDK example: BufferedReader reading text.
 *
 * Domain: a database connection (simulated) and a file reader.
 *
 * How to run:
 *   java Example04_TryWithResources.java
 *
 * Output:
 *   === Old way: try/finally ===
 *   [DB-1] connection opened
 *   [DB-1] running: SELECT * FROM orders
 *   [DB-1] connection closed
 *   === New way: try-with-resources ===
 *   [DB-2] connection opened
 *   [DB-2] running: SELECT * FROM products
 *   [DB-2] connection closed
 *   === Two resources (closed in reverse order) ===
 *   [DB-3] connection opened
 *   [DB-4] connection opened
 *   [DB-3] running: copy data to DB-4
 *   [DB-4] connection closed
 *   [DB-3] connection closed
 *   === Failure inside the body: still closed ===
 *   [DB-5] connection opened
 *   [DB-5] connection closed
 *   Caught: Query failed: table 'payments' not found
 *   === Body AND close() fail: suppressed exception ===
 *   [DB-6] connection opened
 *   Main exception: Query failed: timeout
 *   Suppressed    : Close failed for DB-6
 *   === Real JDK resource: BufferedReader ===
 *   Line: Pen,20
 *   Line: Book,250
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class Example04_TryWithResources {

    public static void main(String[] args) throws IOException {
        System.out.println("=== Old way: try/finally ===");
        DbConnection old = null;
        try {
            old = new DbConnection("DB-1", false);
            old.query("SELECT * FROM orders");
        } finally {
            if (old != null) {        // must check for null: the constructor itself could have failed
                old.close();
            }
        }

        System.out.println("=== New way: try-with-resources ===");
        try (DbConnection conn = new DbConnection("DB-2", false)) {
            conn.query("SELECT * FROM products");
        }   // conn.close() is called automatically right here

        System.out.println("=== Two resources (closed in reverse order) ===");
        try (DbConnection source = new DbConnection("DB-3", false);
             DbConnection target = new DbConnection("DB-4", false)) {
            source.query("copy data to " + target.getName());
        }

        System.out.println("=== Failure inside the body: still closed ===");
        try (DbConnection conn = new DbConnection("DB-5", false)) {
            conn.failingQuery("table 'payments' not found");
        } catch (IllegalStateException e) {
            // Notice: "connection closed" was printed BEFORE we got here.
            System.out.println("Caught: " + e.getMessage());
        }

        System.out.println("=== Body AND close() fail: suppressed exception ===");
        try (DbConnection conn = new DbConnection("DB-6", true)) {
            conn.failingQuery("timeout");
        } catch (IllegalStateException e) {
            System.out.println("Main exception: " + e.getMessage());
            for (Throwable s : e.getSuppressed()) {
                System.out.println("Suppressed    : " + s.getMessage());
            }
        }

        System.out.println("=== Real JDK resource: BufferedReader ===");
        // In Topic 16 you will read real files. StringReader reads from a String, so no file is needed here.
        try (BufferedReader reader = new BufferedReader(new StringReader("Pen,20\nBook,250"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Line: " + line);
            }
        }
    }
}

// Implementing AutoCloseable is all it takes to work with try-with-resources.
class DbConnection implements AutoCloseable {

    private final String name;
    private final boolean failOnClose;

    DbConnection(String name, boolean failOnClose) {
        this.name = name;
        this.failOnClose = failOnClose;
        System.out.println("[" + name + "] connection opened");
    }

    String getName() {
        return name;
    }

    void query(String sql) {
        System.out.println("[" + name + "] running: " + sql);
    }

    void failingQuery(String reason) {
        throw new IllegalStateException("Query failed: " + reason);
    }

    @Override
    public void close() {   // AutoCloseable.close() may throw Exception; ours throws nothing checked
        if (failOnClose) {
            throw new IllegalStateException("Close failed for " + name);
        }
        System.out.println("[" + name + "] connection closed");
    }
}
