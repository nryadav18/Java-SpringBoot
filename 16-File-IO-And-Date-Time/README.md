# 16 · File I/O & Date-Time

Reading and writing files (modern `java.nio.file` and classic `java.io`), parsing CSV,
a short look at serialization (and why JSON replaced it), and the `java.time` API
(`LocalDate`, `LocalDateTime`, `ZonedDateTime`, `Instant`, `Duration`, `Period`,
`DateTimeFormatter`) that your JPA entities will use later.

Open `lesson.html` in your browser for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `Example01_NioFilesBasics.java` | `Path` + `Files`: create, write, append, read, `Files.lines`, copy, move, delete, list |
| `Example02_BufferedReaderWriter.java` | Classic `BufferedReader`/`BufferedWriter`, try-with-resources, `java.io.File` |
| `Example03_ReadingCsv.java` | Parse `students.csv` into records, handle bad rows, report with streams, write a CSV |
| `Example04_SerializationIntro.java` | `Serializable`, `transient`, `serialVersionUID`, Object streams — vs JSON |
| `Example05_DateTimeBasics.java` | `LocalDate`, `LocalTime`, `LocalDateTime`, `ZonedDateTime`, `Instant` |
| `Example06_DurationPeriodFormatting.java` | `Period`, `Duration`, `ChronoUnit`, `DateTimeFormatter` format/parse |
| `Example07_LibraryLoanTracker.java` | Mini program: loans CSV + due dates + fines + written report |
| `students.csv`, `loans.csv` | Input data for examples 03 and 07 |

## How to run
```
cd "D:\Java-SpringBoot\16-File-IO-And-Date-Time"
java Example01_NioFilesBasics.java
```
Examples 01–04 and 07 create an `output/` folder here with the files they write.
It is safe to delete it at any time. Expected output is in each file's `/* Output: */` comment.
