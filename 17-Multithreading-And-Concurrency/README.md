# 17 · Multithreading & Concurrency

Doing many things at the same time in Java: threads, thread pools, safe shared data,
CompletableFuture - and why it matters for Spring Boot web servers (each request = a thread).

Open `lesson.html` in your browser for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `Example01_CreatingThreads.java` | 3 ways to create a thread (extend Thread, Runnable, lambda), start() vs run() |
| `Example02_SleepJoinAndLifecycle.java` | Thread states (NEW, RUNNABLE, TIMED_WAITING, TERMINATED), sleep, join, interrupt |
| `Example03_RaceConditionAndSynchronized.java` | A lost-update race condition and the fix with `synchronized` |
| `Example04_VolatileFlag.java` | `volatile` stop flag (visibility between threads) |
| `Example05_ExecutorServiceAndFuture.java` | Thread pools, Runnable vs Callable, Future, invokeAll, shutdown |
| `Example06_CompletableFuture.java` | supplyAsync, thenApply, thenCombine, exceptionally, allOf, join |
| `Example07_ConcurrentHashMapAndAtomic.java` | AtomicInteger, ConcurrentHashMap vs HashMap, computeIfAbsent cache |
| `Example08_DeadlockDemo.java` | A real deadlock (safely detected) and the lock-ordering fix |
| `Example09_WebServerSimulation.java` | A pretend web server: why Spring singletons must be stateless |

## How to run
```
cd "D:\Java-SpringBoot\17-Multithreading-And-Concurrency"
java Example01_CreatingThreads.java
```
(Java 17, single-file launch - no compile step needed.) Output order may vary between runs: that is normal with threads.
