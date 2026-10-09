/*
 * Example05_DateTimeBasics.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   The java.time API (Java 8+) - the ONLY date/time API you should use today.
 *     LocalDate      - a date without time       2026-03-15         (birthday, due date)
 *     LocalTime      - a time without date       09:30              (shop opening time)
 *     LocalDateTime  - date + time, NO time zone 2026-03-15T09:30   (order placed at...)
 *     ZonedDateTime  - date + time + time zone   ...+05:30[Asia/Kolkata]
 *     Instant        - an exact moment on the global timeline (UTC), for machines/logs
 *   All of them are IMMUTABLE: plusDays() returns a NEW object.
 *
 * HOW TO RUN:
 *   java Example05_DateTimeBasics.java
 */
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Example05_DateTimeBasics {

    public static void main(String[] args) {

        // ---------- "now" - these lines print YOUR current date/time (output varies) ----------
        System.out.println("now (varies): " + LocalDate.now() + " | " + LocalTime.now().withNano(0)
                + " | " + LocalDateTime.now().withNano(0));

        // ---------- LocalDate ----------
        LocalDate release = LocalDate.of(2026, 3, 15);              // year, month, day
        LocalDate sameDay = LocalDate.of(2026, Month.MARCH, 15);    // month as enum - clearer
        LocalDate parsed = LocalDate.parse("2026-12-25");           // ISO text yyyy-MM-dd
        System.out.println("LocalDate     : " + release + ", equal? " + release.equals(sameDay) + ", parsed " + parsed);

        System.out.println("  day of week : " + release.getDayOfWeek());
        System.out.println("  day of year : " + release.getDayOfYear());
        System.out.println("  month/len   : " + release.getMonth() + " has " + release.lengthOfMonth() + " days");
        System.out.println("  leap year?  : " + release.isLeapYear() + " (2028? " + LocalDate.of(2028, 1, 1).isLeapYear() + ")");

        // Date maths - always returns a NEW date (immutable!)
        LocalDate dueDate = release.plusDays(14);
        System.out.println("  +14 days    : " + dueDate);
        System.out.println("  +1 month    : " + release.plusMonths(1));
        System.out.println("  -1 year     : " + release.minusYears(1));
        System.out.println("  Jan 31 + 1 month = " + LocalDate.of(2026, 1, 31).plusMonths(1) + " (clamped to the last valid day)");
        System.out.println("  first of mon: " + release.withDayOfMonth(1));
        System.out.println("  original still: " + release + " (unchanged)");

        // Comparing
        System.out.println("  release before due? " + release.isBefore(dueDate) + ", after? " + release.isAfter(dueDate));
        boolean weekend = release.getDayOfWeek() == DayOfWeek.SATURDAY || release.getDayOfWeek() == DayOfWeek.SUNDAY;
        System.out.println("  is weekend? " + weekend);

        // ---------- LocalTime ----------
        LocalTime opens = LocalTime.of(9, 30);
        LocalTime closes = LocalTime.parse("21:00");
        LocalTime orderTime = LocalTime.of(22, 15);
        System.out.println("LocalTime     : opens " + opens + ", closes " + closes);
        System.out.println("  open at 22:15? " + (!orderTime.isBefore(opens) && orderTime.isBefore(closes)));
        System.out.println("  23:30 + 2h  : " + LocalTime.of(23, 30).plusHours(2) + " (wraps around midnight)");

        // ---------- LocalDateTime ----------
        LocalDateTime placedAt = LocalDateTime.of(2026, 3, 15, 18, 45);
        LocalDateTime deliveryBy = placedAt.plusHours(30);
        System.out.println("LocalDateTime : placed " + placedAt + ", deliver by " + deliveryBy);
        System.out.println("  date part   : " + placedAt.toLocalDate() + ", time part: " + placedAt.toLocalTime());
        System.out.println("  combine     : " + release.atTime(opens));

        // ---------- ZonedDateTime: the same moment in different places ----------
        ZoneId india = ZoneId.of("Asia/Kolkata");
        ZoneId newYork = ZoneId.of("America/New_York");
        ZonedDateTime meetingIndia = ZonedDateTime.of(2026, 1, 15, 10, 0, 0, 0, india);
        ZonedDateTime meetingNY = meetingIndia.withZoneSameInstant(newYork);
        System.out.println("ZonedDateTime : " + meetingIndia);
        System.out.println("  in New York : " + meetingNY);
        System.out.println("  same moment?  " + meetingIndia.isEqual(meetingNY));

        // ---------- Instant: a point on the UTC timeline ----------
        Instant epoch = Instant.ofEpochSecond(0);                     // 1970-01-01T00:00:00Z
        Instant meetingInstant = meetingIndia.toInstant();            // the 'Z' means UTC
        System.out.println("Instant       : epoch " + epoch + ", meeting " + meetingInstant);
        System.out.println("  epoch millis: " + meetingInstant.toEpochMilli());
        System.out.println("  Instant.now() is used for timestamps in logs/DB (varies): " + (Instant.now().isAfter(epoch)));
    }
}

/* Output:
now (varies): 2026-10-09 | 14:05:31 | 2026-10-09T14:05:31
LocalDate     : 2026-03-15, equal? true, parsed 2026-12-25
  day of week : SUNDAY
  day of year : 74
  month/len   : MARCH has 31 days
  leap year?  : false (2028? true)
  +14 days    : 2026-03-29
  +1 month    : 2026-04-15
  -1 year     : 2025-03-15
  Jan 31 + 1 month = 2026-02-28 (clamped to the last valid day)
  first of mon: 2026-03-01
  original still: 2026-03-15 (unchanged)
  release before due? true, after? false
  is weekend? true
LocalTime     : opens 09:30, closes 21:00
  open at 22:15? false
  23:30 + 2h  : 01:30 (wraps around midnight)
LocalDateTime : placed 2026-03-15T18:45, deliver by 2026-03-17T00:45
  date part   : 2026-03-15, time part: 18:45
  combine     : 2026-03-15T09:30
ZonedDateTime : 2026-01-15T10:00+05:30[Asia/Kolkata]
  in New York : 2026-01-14T23:30-05:00[America/New_York]
  same moment?  true
Instant       : epoch 1970-01-01T00:00:00Z, meeting 2026-01-15T04:30:00Z
  epoch millis: 1768451400000
  Instant.now() is used for timestamps in logs/DB (varies): true
*/
