/*
 * Example02_BufferedReaderWriter.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   The classic java.io way (still found in lots of existing code):
 *   - Byte streams vs character streams (InputStream vs Reader)
 *   - BufferedWriter: write line by line (newLine())
 *   - BufferedReader: read line by line (readLine() until null)
 *   - try-with-resources: files are ALWAYS closed, even on errors
 *   - Files.newBufferedReader/newBufferedWriter: the NIO shortcut
 *   - the old java.io.File class (for recognising it in old code)
 *
 *   Domain: a bank account's transaction log.
 *
 * HOW TO RUN (from this folder):
 *   java Example02_BufferedReaderWriter.java
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Example02_BufferedReaderWriter {

    public static void main(String[] args) {
        Path folder = Path.of("output");
        Path logFile = folder.resolve("transactions.log");

        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            System.out.println("Could not create folder: " + e.getMessage());
            return;
        }

        // ---------- 1. Writing with BufferedWriter (classic java.io) ----------
        // FileWriter writes characters to a file; BufferedWriter collects them in
        // memory and writes in big chunks = MUCH faster than one char at a time.
        // 'true' = append mode. We use false here to start fresh each run.
        // try-with-resources: the writer is closed automatically at the end of the try.
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(logFile.toFile(), StandardCharsets.UTF_8, false))) {
            writer.write("DEPOSIT,5000");
            writer.newLine();
            writer.write("WITHDRAW,1200");
            writer.newLine();
            writer.write("DEPOSIT,800");
            writer.newLine();
            writer.write("WITHDRAW,300");
            writer.newLine();
            System.out.println("1) Wrote 4 transactions");
        } catch (IOException e) {
            System.out.println("Write failed: " + e.getMessage());
        }

        // ---------- 2. Reading with BufferedReader (classic java.io) ----------
        double balance = 0;
        int lineNo = 0;
        try (BufferedReader reader = new BufferedReader(
                new FileReader(logFile.toFile(), StandardCharsets.UTF_8))) {
            String line;
            // readLine() returns null at the END of the file
            while ((line = reader.readLine()) != null) {
                lineNo++;
                String[] parts = line.split(",");
                String type = parts[0];
                double amount = Double.parseDouble(parts[1]);
                balance += type.equals("DEPOSIT") ? amount : -amount;
                System.out.printf("2) line %d: %-8s %7.2f -> balance %8.2f%n", lineNo, type, amount, balance);
            }
        } catch (IOException e) {
            System.out.println("Read failed: " + e.getMessage());
        }

        // ---------- 3. The NIO shortcut: Files.newBufferedWriter / newBufferedReader ----------
        Path summary = folder.resolve("summary.txt");
        try (BufferedWriter w = Files.newBufferedWriter(summary, StandardCharsets.UTF_8)) {
            w.write("Transactions: " + lineNo);
            w.newLine();
            w.write("Final balance: " + balance);
            w.newLine();
        } catch (IOException e) {
            System.out.println("Write failed: " + e.getMessage());
        }

        try (BufferedReader r = Files.newBufferedReader(summary, StandardCharsets.UTF_8)) {
            r.lines().forEach(l -> System.out.println("3) " + l));   // BufferedReader.lines() -> Stream
        } catch (IOException e) {
            System.out.println("Read failed: " + e.getMessage());
        }

        // ---------- 4. Two resources in one try: copy a file line by line ----------
        Path copy = folder.resolve("transactions-copy.log");
        try (BufferedReader in = Files.newBufferedReader(logFile, StandardCharsets.UTF_8);
             BufferedWriter out = Files.newBufferedWriter(copy, StandardCharsets.UTF_8)) {
            String line;
            int count = 0;
            while ((line = in.readLine()) != null) {
                out.write(line.toLowerCase());
                out.newLine();
                count++;
            }
            System.out.println("4) Copied " + count + " lines (resources closed in REVERSE order: out, then in)");
        } catch (IOException e) {
            System.out.println("Copy failed: " + e.getMessage());
        }

        // ---------- 5. The old java.io.File class ----------
        // You'll see it in older code. Prefer Path + Files in new code.
        File oldStyle = new File("output", "summary.txt");
        System.out.println("5) File.exists(): " + oldStyle.exists() + ", name: " + oldStyle.getName()
                + ", toPath(): " + oldStyle.toPath().getFileName());

        // ---------- 6. What happens when the file is missing ----------
        try (BufferedReader r = new BufferedReader(new FileReader("output/missing.log", StandardCharsets.UTF_8))) {
            System.out.println(r.readLine());
        } catch (IOException e) {
            System.out.println("6) " + e.getClass().getSimpleName() + " caught - always handle IOException!");
        }
    }
}

/* Output:
1) Wrote 4 transactions
2) line 1: DEPOSIT  5000.00 -> balance  5000.00
2) line 2: WITHDRAW 1200.00 -> balance  3800.00
2) line 3: DEPOSIT   800.00 -> balance  4600.00
2) line 4: WITHDRAW  300.00 -> balance  4300.00
3) Transactions: 4
3) Final balance: 4300.0
4) Copied 4 lines (resources closed in REVERSE order: out, then in)
5) File.exists(): true, name: summary.txt, toPath(): summary.txt
6) FileNotFoundException caught - always handle IOException!
*/
