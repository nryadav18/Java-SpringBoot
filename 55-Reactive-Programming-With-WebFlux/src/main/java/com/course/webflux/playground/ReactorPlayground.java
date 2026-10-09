package com.course.webflux.playground;

/*
 * ReactorPlayground - learn Mono and Flux WITHOUT any web server.
 * -------------------------------------------------------------
 * Run it in VS Code: open this file and click "Run" above main().
 *
 * Here we are in a plain main() method, so using block()/sleep is OK to keep
 * the program alive long enough to see the output. NEVER do this inside WebFlux.
 */
import java.time.Duration;
import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactorPlayground {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("== 1. Nothing happens until you subscribe ==");
        Flux<String> fruits = Flux.just("apple", "banana", "cherry")
                .doOnNext(f -> System.out.println("  producing " + f));
        System.out.println("  (pipeline built, nothing printed yet)");
        fruits.subscribe(f -> System.out.println("  got " + f));
        // Output:
        //   (pipeline built, nothing printed yet)
        //   producing apple
        //   got apple
        //   producing banana ... and so on

        System.out.println("== 2. map and filter ==");
        Flux.range(1, 10)                     // 1..10
                .filter(n -> n % 2 == 0)      // keep even numbers
                .map(n -> n * n)              // square them
                .subscribe(n -> System.out.print(n + " "));
        System.out.println();
        // Output: 4 16 36 64 100

        System.out.println("== 3. Mono: zero or one value ==");
        Mono.just("Asha").map(String::toUpperCase).subscribe(System.out::println);  // ASHA
        Mono.<String>empty().defaultIfEmpty("nobody").subscribe(System.out::println); // nobody

        System.out.println("== 4. flatMap: each element starts another async call ==");
        Flux.just(1L, 2L, 3L)
                .flatMap(ReactorPlayground::findUserName)   // returns Mono<String> per id
                .collectList()
                .subscribe(names -> System.out.println("  users: " + names));
        Thread.sleep(500); // wait for the delayed Monos (only OK in a demo main!)
        // Output: users: [user-3, user-2, user-1]
        // (flatMap does NOT keep order - the fastest answer comes first. Use concatMap to keep order.)

        System.out.println("== 5. zip: combine two sources pair by pair ==");
        Flux<String> names = Flux.just("Laptop", "Mouse");
        Flux<Integer> prices = Flux.just(900, 20);
        Flux.zip(names, prices, (n, p) -> n + " costs " + p).subscribe(System.out::println);
        // Output: Laptop costs 900 / Mouse costs 20

        System.out.println("== 6. Errors: onErrorResume gives a fallback ==");
        Flux.just(10, 5, 0, 2)
                .map(n -> 100 / n)                         // 100/0 -> ArithmeticException!
                .onErrorResume(ex -> Flux.just(-1))        // replace the error with -1 and finish
                .subscribe(n -> System.out.print(n + " "));
        System.out.println();
        // Output: 10 20 -1      (2 is never processed: an error ENDS the stream)

        System.out.println("== 7. delayElements: time-based streams ==");
        List<String> collected = Flux.just("tick", "tock", "tick")
                .delayElements(Duration.ofMillis(100))
                .collectList()
                .block(); // OK here: plain main method, not a web request
        System.out.println("  " + collected);
        // Output: [tick, tock, tick]  (after ~300 ms)

        System.out.println("== 8. Backpressure: subscriber asks for small batches ==");
        Flux.range(1, 6)
                .doOnRequest(n -> System.out.println("  upstream was asked for " + n))
                .limitRate(2)
                .subscribe(n -> System.out.println("  received " + n));
        // Output shows "asked for 2" and then a few items, again and again.
    }

    // Pretend database/HTTP lookup that takes some time (id 1 is the slowest).
    private static Mono<String> findUserName(Long id) {
        return Mono.just("user-" + id).delayElement(Duration.ofMillis(100 * (4 - id)));
    }
}
