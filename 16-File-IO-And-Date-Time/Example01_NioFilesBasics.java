/*
 * Example01_NioFilesBasics.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   The modern file API java.nio.file (Java 7+, improved in 11):
 *     Path      - "the address" of a file or folder
 *     Files     - static helper methods that DO things with paths
 *   - Path.of, resolve, getFileName, getParent
 *   - Files.createDirectories, writeString, readString, write (APPEND),
 *     readAllLines, lines (a Stream!), exists, size, copy, move, delete, list
 *
 *   It creates a folder called "output" next to where you run it.
 *   Feel free to open the files in VS Code and delete the folder afterwards.
 *
 * HOW TO RUN (from this folder):
 *   java Example01_NioFilesBasics.java
 */
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Example01_NioFilesBasics {

    // File operations can fail (disk full, no permission, file missing...),
    // so most Files methods throw the CHECKED exception IOException.
    // For a small demo we simply declare 'throws IOException' on main.
    public static void main(String[] args) throws IOException {

        // ---------- 1. Paths: just addresses, nothing is created yet ----------
        Path outputDir = Path.of("output");                     // relative to the current folder
        Path notes = outputDir.resolve("notes.txt");            // output/notes.txt
        Path nested = Path.of("output", "archive", "2026");     // output/archive/2026 (OS-correct separators)

        System.out.println("1) file name : " + notes.getFileName());
        System.out.println("1) parent    : " + notes.getParent());
        System.out.println("1) nested    : " + nested.getNameCount() + " parts, last = " + nested.getFileName());
        System.out.println("1) absolute? : " + notes.isAbsolute() + " (use toAbsolutePath() to see the full path)");

        // ---------- 2. Create folders (no error if they already exist) ----------
        Files.createDirectories(nested);
        System.out.println("2) folder exists: " + Files.exists(nested));

        // ---------- 3. Write a whole String to a file (creates or REPLACES it) ----------
        Files.writeString(notes, "Buy milk\nCall the bank\n", StandardCharsets.UTF_8);

        // APPEND instead of replacing
        Files.writeString(notes, "Finish Java topic 16\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);

        // Write a list of lines at once
        Path shopping = outputDir.resolve("shopping.txt");
        Files.write(shopping, List.of("Rice", "Dal", "Milk", "Eggs", "Bread"), StandardCharsets.UTF_8);

        // ---------- 4. Read ----------
        String all = Files.readString(notes, StandardCharsets.UTF_8);      // whole file as ONE String
        System.out.println("4) readString:\n" + all.strip());

        List<String> lines = Files.readAllLines(shopping, StandardCharsets.UTF_8);  // List of lines
        System.out.println("4) readAllLines: " + lines + " (" + lines.size() + " lines)");

        // Files.lines returns a LAZY Stream - good for BIG files (does not load everything).
        // It keeps the file open, so ALWAYS use try-with-resources to close it.
        try (Stream<String> stream = Files.lines(shopping, StandardCharsets.UTF_8)) {
            String withE = stream.filter(l -> l.contains("e"))
                    .map(String::toUpperCase)
                    .collect(Collectors.joining(", "));
            System.out.println("4) lines containing 'e': " + withE);
        }

        // ---------- 5. File information ----------
        System.out.println("5) size of shopping.txt: " + Files.size(shopping) + " bytes");
        System.out.println("5) is directory? " + Files.isDirectory(outputDir) + ", is regular file? " + Files.isRegularFile(shopping));

        // ---------- 6. Copy, move, delete ----------
        Path backup = nested.resolve("shopping-backup.txt");
        Files.copy(shopping, backup, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("6) copied     : " + Files.exists(backup));

        Path renamed = outputDir.resolve("groceries.txt");
        Files.move(shopping, renamed, StandardCopyOption.REPLACE_EXISTING);   // move = rename
        System.out.println("6) moved      : old exists? " + Files.exists(shopping) + ", new exists? " + Files.exists(renamed));

        boolean deleted = Files.deleteIfExists(backup);
        System.out.println("6) deleted    : " + deleted);

        // ---------- 7. List a folder (also a Stream -> try-with-resources) ----------
        try (Stream<Path> entries = Files.list(outputDir)) {
            String names = entries.map(p -> p.getFileName().toString())
                    .sorted()
                    .collect(Collectors.joining(", "));
            System.out.println("7) output folder contains: " + names);
        }

        // ---------- 8. Reading a missing file -> exception ----------
        try {
            Files.readString(Path.of("output", "does-not-exist.txt"));
        } catch (java.nio.file.NoSuchFileException e) {
            System.out.println("8) NoSuchFileException: " + e.getMessage());
        }
    }
}

/* Output:
1) file name : notes.txt
1) parent    : output
1) nested    : 3 parts, last = 2026
1) absolute? : false (use toAbsolutePath() to see the full path)
2) folder exists: true
4) readString:
Buy milk
Call the bank
Finish Java topic 16
4) readAllLines: [Rice, Dal, Milk, Eggs, Bread] (5 lines)
4) lines containing 'e': RICE, BREAD
5) size of shopping.txt: 30 bytes
5) is directory? true, is regular file? true
6) copied     : true
6) moved      : old exists? false, new exists? true
6) deleted    : true
7) output folder contains: archive, groceries.txt, notes.txt
8) NoSuchFileException: output\does-not-exist.txt
*/
// Note: the size is 25 bytes on Linux/macOS (lines end with \n there, \r\n on Windows),
// and the path in 8) shows / instead of \ on Linux/macOS.
