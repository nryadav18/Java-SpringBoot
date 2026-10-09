# 14 · Streams API & Optional

Streams let you process collections in a readable "pipeline" style
(filter → transform → collect) instead of hand-written loops.
`Optional` is a box that may or may not hold a value — Spring Data's
`findById(...)` returns one, so you will use it in every Spring project.

Open `lesson.html` in your browser for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `Example01_StreamBasics.java` | Loop vs stream, creating streams, laziness, single use |
| `Example02_IntermediateOperations.java` | filter, map, flatMap, distinct, sorted, limit, skip (pagination), mapToDouble |
| `Example03_TerminalOperations.java` | forEach, collect, toList(), toMap, count, reduce, min/max, any/all/noneMatch, findFirst/findAny |
| `Example04_Collectors.java` | groupingBy (+counting, averaging, summing, mapping), partitioningBy, joining, summarizing, toMap merge |
| `Example05_PrimitiveAndParallelStreams.java` | IntStream/LongStream, range, statistics, iterate/generate, parallel streams with caution |
| `Example06_Optional.java` | Optional API + a fake `findById` repository like Spring Data |
| `Example07_OrderReport.java` | Mini program: full sales report using only streams |

## How to run
```
cd "D:\Java-SpringBoot\14-Streams-API-And-Optional"
java Example01_StreamBasics.java
```
Every file ends with an `/* Output: ... */` comment showing what you should see.
