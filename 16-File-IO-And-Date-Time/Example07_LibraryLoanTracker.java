/*
 * Example07_LibraryLoanTracker.java
 * ------------------------------------------------------------
 * MINI PROGRAM: a library loan tracker that combines FILE I/O + DATE/TIME.
 *
 *   1. Reads loans.csv (member, book, borrowedOn, returnedOn) - dates in dd-MM-yyyy
 *   2. Calculates each due date (borrowed + 14 days)
 *   3. Works out late days and fine (Rs.5 per late day) as of a report date
 *   4. Writes output/overdue-report.txt with a timestamp header
 *
 *   The "report date" is fixed (15-03-2026) so the output is always the same.
 *   In a real app you'd use LocalDate.now().
 *
 * HOW TO RUN (from this folder):
 *   java Example07_LibraryLoanTracker.java
 */
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Example07_LibraryLoanTracker {

    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    static final int LOAN_DAYS = 14;
    static final int FINE_PER_DAY = 5;

    // returnedOn is null when the book has not come back yet
    record Loan(String member, String book, LocalDate borrowedOn, LocalDate returnedOn) {

        LocalDate dueDate() {
            return borrowedOn.plusDays(LOAN_DAYS);
        }

        boolean isReturned() {
            return returnedOn != null;
        }

        // Late days are counted up to the return date, or up to "today" if not returned
        long lateDays(LocalDate today) {
            LocalDate end = isReturned() ? returnedOn : today;
            return Math.max(0, ChronoUnit.DAYS.between(dueDate(), end));
        }
    }

    public static void main(String[] args) throws IOException {
        LocalDate reportDate = LocalDate.of(2026, 3, 15);
        Path csv = Files.exists(Path.of("loans.csv"))
                ? Path.of("loans.csv")
                : Path.of("16-File-IO-And-Date-Time", "loans.csv");

        // Files.lines -> Stream; skip(1) skips the header; try-with-resources closes the file
        List<Loan> loans = new ArrayList<>();
        try (Stream<String> lines = Files.lines(csv, StandardCharsets.UTF_8)) {
            lines.skip(1)
                    .filter(line -> !line.isBlank())
                    .forEach(line -> loans.add(parse(line)));
        }

        System.out.println("Library report as of " + reportDate.format(DATE_FORMAT));
        System.out.println("-".repeat(78));
        System.out.printf("%-7s %-30s %-11s %-11s %-9s %s%n", "Member", "Book", "Borrowed", "Due", "Status", "Fine");
        long totalFine = 0;
        List<String> overdueLines = new ArrayList<>();
        for (Loan loan : loans) {
            long late = loan.lateDays(reportDate);
            long fine = late * FINE_PER_DAY;
            totalFine += fine;
            String status = loan.isReturned() ? "RETURNED" : (late > 0 ? "OVERDUE" : "ON LOAN");
            System.out.printf("%-7s %-30s %-11s %-11s %-9s Rs.%d%n", loan.member(), loan.book(),
                    loan.borrowedOn().format(DATE_FORMAT), loan.dueDate().format(DATE_FORMAT), status, fine);
            if (!loan.isReturned() && late > 0) {
                overdueLines.add(loan.member() + " must return '" + loan.book() + "' - " + late + " days late");
            }
        }
        System.out.println("-".repeat(78));
        System.out.println("Total fines: Rs." + totalFine);

        // ---------- Write the overdue report ----------
        Path report = Path.of("output", "overdue-report.txt");
        Files.createDirectories(report.getParent());
        LocalDateTime generatedAt = reportDate.atTime(9, 0);   // fixed for stable output
        try (BufferedWriter writer = Files.newBufferedWriter(report, StandardCharsets.UTF_8)) {
            writer.write("Overdue report generated " + generatedAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            writer.newLine();
            for (String line : overdueLines) {
                writer.write(line);
                writer.newLine();
            }
        }
        System.out.println("Overdue report written to output/overdue-report.txt:");
        Files.readAllLines(report, StandardCharsets.UTF_8).forEach(l -> System.out.println("  " + l));
    }

    static Loan parse(String line) {
        // split with limit -1 keeps the EMPTY last column (returnedOn missing)
        String[] c = line.split(",", -1);
        LocalDate returned = c[3].isBlank() ? null : LocalDate.parse(c[3].strip(), DATE_FORMAT);
        return new Loan(c[0].strip(), c[1].strip(), LocalDate.parse(c[2].strip(), DATE_FORMAT), returned);
    }
}

/* Output:
Library report as of 15-03-2026
------------------------------------------------------------------------------
Member  Book                           Borrowed    Due         Status    Fine
Asha    Clean Code                     01-03-2026  15-03-2026  RETURNED  Rs.0
Ravi    Effective Java                 20-02-2026  06-03-2026  OVERDUE   Rs.45
Meena   Head First Java                25-02-2026  11-03-2026  RETURNED  Rs.25
John    Spring in Action               10-03-2026  24-03-2026  ON LOAN   Rs.0
Bala    Java Concurrency in Practice   15-02-2026  01-03-2026  RETURNED  Rs.0
------------------------------------------------------------------------------
Total fines: Rs.70
Overdue report written to output/overdue-report.txt:
  Overdue report generated 15-03-2026 09:00
  Ravi must return 'Effective Java' - 9 days late
*/
