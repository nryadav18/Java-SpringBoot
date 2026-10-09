/*
 * Example03_ReadingCsv.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   Reading a CSV (Comma-Separated Values) file - the most common
 *   "import data from a file" job in real projects.
 *   - read all lines, skip the header row, skip blank lines
 *   - split each line, convert text to numbers/dates
 *   - handle BAD rows without crashing (collect errors instead)
 *   - turn rows into records, then use streams for a report
 *   - write a new CSV file as output
 *
 *   Input file: students.csv (in this folder). It deliberately contains
 *   a blank line and two broken rows so you can see error handling.
 *
 * HOW TO RUN (from this folder):
 *   java Example03_ReadingCsv.java
 */
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Example03_ReadingCsv {

    // One CSV row = one record (records were covered in topic 15)
    record Student(int id, String name, String department, int marks, LocalDate joinedOn) { }

    public static void main(String[] args) throws IOException {
        Path csv = findInputFile("students.csv");
        List<String> lines = Files.readAllLines(csv, StandardCharsets.UTF_8);
        System.out.println("Read " + lines.size() + " lines from " + csv.getFileName());

        List<Student> students = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Start at 1 to SKIP the header row (id,name,department,marks,joinedOn)
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineNumber = i + 1;           // humans count lines from 1

            if (line.isBlank()) {
                continue;                     // ignore empty lines
            }
            String[] cols = line.split(",");
            if (cols.length != 5) {
                errors.add("line " + lineNumber + ": expected 5 columns but found " + cols.length);
                continue;
            }
            try {
                Student s = new Student(
                        Integer.parseInt(cols[0].strip()),
                        cols[1].strip(),
                        cols[2].strip(),
                        Integer.parseInt(cols[3].strip()),
                        LocalDate.parse(cols[4].strip()));     // ISO format yyyy-MM-dd
                students.add(s);
            } catch (NumberFormatException | DateTimeParseException e) {
                errors.add("line " + lineNumber + ": " + e.getMessage());
            }
        }

        System.out.println("Valid students: " + students.size());
        System.out.println("Errors:");
        errors.forEach(err -> System.out.println("  " + err));

        // ---------- Report using streams ----------
        Map<String, Double> avgByDept = students.stream()
                .collect(Collectors.groupingBy(Student::department, TreeMap::new,
                        Collectors.averagingInt(Student::marks)));
        System.out.println("Average marks per department: " + avgByDept);

        Student topper = students.stream()
                .max((a, b) -> Integer.compare(a.marks(), b.marks()))
                .orElseThrow();
        System.out.println("Topper: " + topper.name() + " (" + topper.marks() + ")");

        long joined2024 = students.stream().filter(s -> s.joinedOn().getYear() == 2024).count();
        System.out.println("Joined in 2024: " + joined2024);

        // ---------- Write a result CSV ----------
        List<String> out = new ArrayList<>();
        out.add("name,marks,result");                        // header
        for (Student s : students) {
            out.add(s.name() + "," + s.marks() + "," + (s.marks() >= 60 ? "PASS" : "FAIL"));
        }
        Path outFile = Path.of("output", "results.csv");
        Files.createDirectories(outFile.getParent());
        Files.write(outFile, out, StandardCharsets.UTF_8);
        System.out.println("Wrote " + (out.size() - 1) + " rows to output/results.csv");
    }

    // Works whether you run from THIS folder or from the course root folder
    // (VS Code sometimes runs programs from the workspace root).
    static Path findInputFile(String name) {
        Path here = Path.of(name);
        if (Files.exists(here)) {
            return here;
        }
        return Path.of("16-File-IO-And-Date-Time", name);
    }
}

/* Output:
Read 9 lines from students.csv
Valid students: 5
Errors:
  line 5: For input string: "abc"
  line 9: expected 5 columns but found 4
Average marks per department: {CSE=75.0, ECE=76.0}
Topper: Asha (92)
Joined in 2024: 3
Wrote 5 rows to output/results.csv
*/
