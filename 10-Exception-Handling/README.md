# Topic 10 - Exception Handling

What happens when things go wrong at runtime, and how to handle it cleanly: try/catch/finally,
checked vs unchecked exceptions, try-with-resources, throw vs throws, custom exceptions,
exception chaining and how to read a stack trace. Open `lesson.html` for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `Example01_WhatIsAnException.java` | Common exceptions (/ by zero, bad index, null, bad number) and a stack trace |
| `Example02_TryCatchFinally.java` | try/catch/finally order, multiple catches, multi-catch, finally after return |
| `Example03_CheckedVsUnchecked.java` | Checked (IOException, InterruptedException) vs unchecked vs Error |
| `Example04_TryWithResources.java` | AutoCloseable, automatic closing, reverse close order, suppressed exceptions |
| `Example05_ThrowAndThrows.java` | `throw` for validation, `throws` in signatures, propagation up the call stack |
| `Example06_CustomExceptions.java` | `InsufficientBalanceException` (checked) and `AccountNotFoundException` (unchecked) in a Bank |
| `Example07_ExceptionChaining.java` | Wrapping a low-level exception with a cause, `getCause()`, "Caused by:" |

## How to run (terminal opened in this folder)
```
java Example01_WhatIsAnException.java
java Example06_CustomExceptions.java
```
