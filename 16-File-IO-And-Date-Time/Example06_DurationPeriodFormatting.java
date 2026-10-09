/*
 * Example06_DurationPeriodFormatting.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   Measuring time and converting dates to/from text:
 *     Period            - an amount of DATE-based time (years, months, days)
 *     Duration          - an amount of TIME-based time (hours, minutes, seconds)
 *     ChronoUnit        - "how many days/hours between A and B"
 *     DateTimeFormatter - format a date as text, parse text into a date
 *
 * HOW TO RUN:
 *   java Example06_DurationPeriodFormatting.java
 */
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Example06_DurationPeriodFormatting {

    public static void main(String[] args) {

        // ---------- Period: human calendar amounts ----------
        LocalDate birthDate = LocalDate.of(1998, 8, 20);
        LocalDate today = LocalDate.of(2026, 3, 15);          // fixed "today" so output is stable
        Period age = Period.between(birthDate, today);
        System.out.println("Period   : age = " + age.getYears() + " years, " + age.getMonths()
                + " months, " + age.getDays() + " days  (" + age + ")");

        Period subscription = Period.ofMonths(6);
        System.out.println("           subscription ends " + today.plus(subscription) + " (" + subscription + ")");

        // ---------- ChronoUnit: total units between two dates ----------
        LocalDate examDay = LocalDate.of(2026, 5, 4);
        System.out.println("ChronoUnit: days until exam = " + ChronoUnit.DAYS.between(today, examDay));
        System.out.println("            weeks until exam = " + ChronoUnit.WEEKS.between(today, examDay));
        System.out.println("            total months alive = " + ChronoUnit.MONTHS.between(birthDate, today));

        // ---------- Duration: clock amounts ----------
        LocalTime shiftStart = LocalTime.of(9, 15);
        LocalTime shiftEnd = LocalTime.of(18, 40);
        Duration worked = Duration.between(shiftStart, shiftEnd);
        System.out.println("Duration : worked " + worked.toHours() + "h " + worked.toMinutesPart() + "m (" + worked + ")");

        Duration movie = Duration.ofMinutes(148);
        System.out.println("           movie 148 min = " + movie.toHoursPart() + "h " + movie.toMinutesPart() + "m");

        LocalDateTime orderPlaced = LocalDateTime.of(2026, 3, 14, 20, 0);
        LocalDateTime delivered = LocalDateTime.of(2026, 3, 15, 13, 30);
        Duration deliveryTime = Duration.between(orderPlaced, delivered);
        System.out.println("           delivery took " + deliveryTime.toHours() + " hours "
                + deliveryTime.toMinutesPart() + " minutes, within 24h? " + (deliveryTime.compareTo(Duration.ofHours(24)) <= 0));

        // ---------- DateTimeFormatter: date -> text ----------
        LocalDateTime meeting = LocalDateTime.of(2026, 3, 15, 16, 5);
        System.out.println("Format   : ISO default         = " + meeting);
        System.out.println("           dd-MM-yyyy           = " + meeting.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        System.out.println("           dd/MM/yyyy HH:mm     = " + meeting.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        System.out.println("           EEE, d MMM yyyy      = "
                + meeting.format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.ENGLISH)));
        System.out.println("           EEEE dd MMMM, hh:mm a = "
                + meeting.format(DateTimeFormatter.ofPattern("EEEE dd MMMM, hh:mm a", Locale.ENGLISH)));
        System.out.println("           ISO_LOCAL_DATE       = " + meeting.format(DateTimeFormatter.ISO_LOCAL_DATE));

        // ---------- DateTimeFormatter: text -> date (parsing) ----------
        DateTimeFormatter indian = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate fromForm = LocalDate.parse("05-09-2026", indian);
        System.out.println("Parse    : '05-09-2026' -> " + fromForm + " (" + fromForm.getMonth() + ")");

        // Bad input -> DateTimeParseException (always handle it for user input!)
        String[] userInputs = {"31-04-2026", "2026/01/10", "10-01-2026"};
        for (String input : userInputs) {
            try {
                LocalDate d = LocalDate.parse(input, indian);
                System.out.println("           '" + input + "' OK -> " + d);
            } catch (DateTimeParseException e) {
                System.out.println("           '" + input + "' INVALID -> " + e.getMessage());
            }
        }

        // ---------- Mini example: library late fee ----------
        LocalDate borrowed = LocalDate.of(2026, 2, 20);
        LocalDate due = borrowed.plusDays(14);
        LocalDate returned = LocalDate.of(2026, 3, 15);
        long lateDays = Math.max(0, ChronoUnit.DAYS.between(due, returned));
        System.out.println("Library  : borrowed " + borrowed.format(indian) + ", due " + due.format(indian)
                + ", returned " + returned.format(indian) + " -> " + lateDays + " days late, fine Rs." + (lateDays * 5));
    }
}

/* Output:
Period   : age = 27 years, 6 months, 23 days  (P27Y6M23D)
           subscription ends 2026-09-15 (P6M)
ChronoUnit: days until exam = 50
            weeks until exam = 7
            total months alive = 330
Duration : worked 9h 25m (PT9H25M)
           movie 148 min = 2h 28m
           delivery took 17 hours 30 minutes, within 24h? true
Format   : ISO default         = 2026-03-15T16:05
           dd-MM-yyyy           = 15-03-2026
           dd/MM/yyyy HH:mm     = 15/03/2026 16:05
           EEE, d MMM yyyy      = Sun, 15 Mar 2026
           EEEE dd MMMM, hh:mm a = Sunday 15 March, 04:05 PM
           ISO_LOCAL_DATE       = 2026-03-15
Parse    : '05-09-2026' -> 2026-09-05 (SEPTEMBER)
           '31-04-2026' OK -> 2026-04-30
           '2026/01/10' INVALID -> Text '2026/01/10' could not be parsed at index 2
           '10-01-2026' OK -> 2026-01-10
Library  : borrowed 20-02-2026, due 06-03-2026, returned 15-03-2026 -> 9 days late, fine Rs.45
*/
