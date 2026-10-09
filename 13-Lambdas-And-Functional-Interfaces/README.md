# 13 · Lambdas & Functional Interfaces

Lambdas let you pass **behaviour** (a small piece of code) around like a value.
They are the foundation for Streams (topic 14) and are used everywhere in Spring
(security config, `RestClient`, event listeners, `Comparator`s in services...).

Open `lesson.html` in your browser for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `Example01_AnonymousClassToLambda.java` | Named class -> anonymous class -> lambda, all doing the same sort |
| `Example02_LambdaSyntax.java` | Functional interfaces, `@FunctionalInterface`, every lambda syntax variant |
| `Example03_BuiltInFunctionalInterfaces.java` | Runnable, Predicate, Function, Consumer, Supplier, BiFunction, UnaryOperator, BinaryOperator, Comparator |
| `Example04_MethodReferences.java` | The 4 kinds of method references (`::`) |
| `Example05_EffectivelyFinalAndComposition.java` | Effectively final rule; `andThen`, `compose`, `and/or/negate`, `Comparator.comparing` |
| `Example06_FoodDeliveryFilters.java` | Mini program: combine filters at runtime in a food-delivery search |

## How to run
```
cd "D:\Java-SpringBoot\13-Lambdas-And-Functional-Interfaces"
java Example01_AnonymousClassToLambda.java
```
Every file ends with an `/* Output: ... */` comment showing what you should see.
