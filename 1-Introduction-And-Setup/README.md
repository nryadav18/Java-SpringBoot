# Topic 1 - Welcome & Setup

What Java is, JDK vs JRE vs JVM, how code becomes bytecode, checking Java 17 / Maven,
VS Code setup, and your first programs. Open `lesson.html` in a browser for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `lesson.html` | The full lesson (start here) |
| `Example01_HelloWorld.java` | Smallest Java program: class, main method, println |
| `Example02_PrintingAndComments.java` | print vs println, escape sequences, `+` joining, printf, comment types |
| `Example03_ReadingInput.java` | Reading text and numbers from the keyboard with `Scanner` |

## How to run
Open a terminal in this folder (VS Code: Terminal -> New Terminal) and run:

```
java Example01_HelloWorld.java
java Example02_PrintingAndComments.java
java Example03_ReadingInput.java
```

Classic two-step way (compile, then run):

```
javac -d out Example01_HelloWorld.java
java -cp out Example01_HelloWorld
```
