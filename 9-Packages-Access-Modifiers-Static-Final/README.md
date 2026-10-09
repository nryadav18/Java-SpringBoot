# Topic 9 - Packages, Access Modifiers, static & final

How to organise Java code into packages, control who can see what (public / protected / default / private),
share data with `static`, lock things down with `final`, use nested/inner/anonymous classes, and write a simple Singleton.
Open `lesson.html` in your browser for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `src/com/shop/App.java` | Entry point in package `com.shop`; imports, static import, fully qualified names, what you may NOT access |
| `src/com/shop/model/Product.java` | All 4 access levels on fields/methods |
| `src/com/shop/model/StockChecker.java` | A package-private (non-public) helper class |
| `src/com/shop/sale/FlashSaleProduct.java` | `protected` used by a child class in another package |
| `src/com/shop/util/PriceUtils.java` | Utility class: `final` class, private constructor, `static final` constant |
| `Example01_StaticMembers.java` | static fields, methods, static block vs instance fields |
| `Example02_FinalKeyword.java` | final variables, final references, final methods, final classes |
| `Example03_NestedAndInnerClasses.java` | static nested, inner, local and anonymous classes |
| `Example04_Singleton.java` | Eager, lazy and enum Singletons |

## How to run (terminal opened in this folder)
```
javac -d out -sourcepath src src/com/shop/App.java
java -cp out com.shop.App

java Example01_StaticMembers.java
```
The `out/` folder holds the compiled `.class` files; you can delete it any time.
