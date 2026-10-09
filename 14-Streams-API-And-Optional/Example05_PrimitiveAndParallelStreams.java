/*
 * Example05_PrimitiveAndParallelStreams.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   PART A - Primitive streams: IntStream, LongStream, DoubleStream
 *            range / rangeClosed, sum, average, max, summaryStatistics,
 *            boxed(), mapToObj, chars(), Stream.iterate / generate.
 *   PART B - Parallel streams: how to create them and WHY to be careful.
 *
 * HOW TO RUN:
 *   java Example05_PrimitiveAndParallelStreams.java
 */
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Example05_PrimitiveAndParallelStreams {

    public static void main(String[] args) {

        // ================= PART A: primitive streams =================

        // range(start, end) -> end is EXCLUSIVE; rangeClosed -> end INCLUSIVE
        System.out.println("range(1,5)       : " + IntStream.range(1, 5).boxed().collect(Collectors.toList()));
        System.out.println("rangeClosed(1,5) : " + IntStream.rangeClosed(1, 5).boxed().collect(Collectors.toList()));

        // A stream replacement for the classic "sum 1..100" loop
        System.out.println("sum 1..100       : " + IntStream.rangeClosed(1, 100).sum());

        // Even numbers 1..20
        System.out.println("evens 1..20      : " + IntStream.rangeClosed(1, 20)
                .filter(n -> n % 2 == 0)
                .boxed()
                .collect(Collectors.toList()));

        // Factorial of 10 with LongStream + reduce
        System.out.println("10!              : " + LongStream.rangeClosed(1, 10).reduce(1, (a, b) -> a * b));

        // average() returns OptionalDouble because the stream could be empty
        int[] marks = {78, 92, 65, 88, 70};
        System.out.println("average marks    : " + IntStream.of(marks).average().getAsDouble());
        System.out.println("max marks        : " + IntStream.of(marks).max().getAsInt());

        // summaryStatistics: everything in one go
        IntSummaryStatistics st = IntStream.of(marks).summaryStatistics();
        System.out.println("stats            : " + st);

        // mapToObj: int -> object. Build a multiplication table line.
        System.out.println("table of 7       : " + IntStream.rangeClosed(1, 5)
                .mapToObj(i -> "7x" + i + "=" + (7 * i))
                .collect(Collectors.joining(" ")));

        // Strings have chars() -> IntStream of character codes
        long vowels = "Spring Boot Developer".toLowerCase().chars()
                .filter(c -> "aeiou".indexOf(c) >= 0)
                .count();
        System.out.println("vowels           : " + vowels);

        // mapToInt on an object stream -> sum of lengths
        List<String> words = List.of("java", "spring", "boot");
        System.out.println("total letters    : " + words.stream().mapToInt(String::length).sum());

        // Infinite streams MUST be limited!
        System.out.println("iterate x2       : " + Stream.iterate(1, n -> n * 2).limit(8).collect(Collectors.toList()));
        System.out.println("generate         : " + Stream.generate(() -> "hi").limit(3).collect(Collectors.toList()));
        // Java 9+: iterate with a stop condition (like a for loop)
        System.out.println("iterate(stop)    : " + Stream.iterate(10, n -> n <= 50, n -> n + 10).collect(Collectors.toList()));

        // ================= PART B: parallel streams =================
        // .parallelStream() or .parallel() splits work across CPU cores.
        long sequentialSum = LongStream.rangeClosed(1, 1_000_000).sum();
        long parallelSum = LongStream.rangeClosed(1, 1_000_000).parallel().sum();
        System.out.println("sequential sum   : " + sequentialSum);
        System.out.println("parallel sum     : " + parallelSum + " (same answer)");

        // ORDER: forEach on a parallel stream prints in a RANDOM order.
        // forEachOrdered keeps the original order (but loses some speed).
        System.out.print("forEachOrdered   : ");
        List.of(1, 2, 3, 4, 5).parallelStream().forEachOrdered(n -> System.out.print(n + " "));
        System.out.println();

        // SAFE way to gather results from a parallel stream: collect(), NOT
        // adding to a shared ArrayList from inside forEach (that is a race condition).
        List<Integer> squares = IntStream.rangeClosed(1, 10).parallel()
                .map(n -> n * n)
                .boxed()
                .collect(Collectors.toList());     // collect keeps encounter order
        System.out.println("parallel squares : " + squares);
        System.out.println("CPU cores here   : (depends on your machine) -> use parallel only for big, CPU-heavy work");
    }
}

/* Output:
range(1,5)       : [1, 2, 3, 4]
rangeClosed(1,5) : [1, 2, 3, 4, 5]
sum 1..100       : 5050
evens 1..20      : [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]
10!              : 3628800
average marks    : 78.6
max marks        : 92
stats            : IntSummaryStatistics{count=5, sum=393, min=65, average=78.600000, max=92}
table of 7       : 7x1=7 7x2=14 7x3=21 7x4=28 7x5=35
vowels           : 7
total letters    : 14
iterate x2       : [1, 2, 4, 8, 16, 32, 64, 128]
generate         : [hi, hi, hi]
iterate(stop)    : [10, 20, 30, 40, 50]
sequential sum   : 500000500000
parallel sum     : 500000500000 (same answer)
forEachOrdered   : 1 2 3 4 5 
parallel squares : [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]
CPU cores here   : (depends on your machine) -> use parallel only for big, CPU-heavy work
*/
