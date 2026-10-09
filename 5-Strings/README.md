# Topic 5 - Strings

Text is everywhere in real programs: names, emails, JSON, SQL, log messages.
This topic teaches how Java `String`s really work (immutability, the String Pool,
`==` vs `equals`), the everyday String methods, formatting, text blocks,
`StringBuilder` / `StringBuffer`, `char` handling, and the classic interview string programs.

Open **lesson.html** in your browser for the full lesson.

## Files

| File | What it shows |
|------|---------------|
| `Example01_StringBasicsAndImmutability.java` | Creating strings, immutability, String Pool, `==` vs `equals`, `compareTo` |
| `Example02_CommonStringMethods.java` | length, charAt, substring, indexOf, contains, replace, split, trim/strip, isBlank, join, repeat |
| `Example03_FormattingAndTextBlocks.java` | `String.format`, `formatted`, `printf` tables, text blocks |
| `Example04_StringBuilderPerformance.java` | StringBuilder methods, `+` in a loop vs StringBuilder timing, StringBuffer |
| `Example05_CharHandling.java` | `char` vs String, chars as numbers, `Character` helpers, password checker |
| `Example06_InterviewStringPrograms.java` | Reverse, palindrome, vowels, anagram, word count, first non-repeating char |

## How to run

```
cd "D:\Java-SpringBoot\5-Strings"
java Example01_StringBasicsAndImmutability.java
```

Each file prints its own output, and the expected output is written at the bottom of each file.
