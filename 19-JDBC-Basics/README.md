# 19 · JDBC Basics

How a Java program talks to a relational database with plain JDBC: connections, SQL, PreparedStatement,
ResultSet, a CRUD DAO, SQL injection and transactions. Uses an H2 in-memory database, so nothing needs installing.
Open `lesson.html` in your browser for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `pom.xml` | Maven project: H2 2.3.232 dependency + exec-maven-plugin 3.5.0 |
| `src/main/java/com/course/jdbcbasics/Database.java` | JDBC URL, `DriverManager.getConnection`, creating tables ("schema.sql" in code) |
| `.../Student.java` | A record for one row of the `students` table |
| `.../StudentDao.java` | CRUD with `PreparedStatement` + `ResultSet` (save, findById, findAll, update, delete, count) |
| `.../SqlInjectionDemo.java` | `Statement` + string concatenation (unsafe) vs `PreparedStatement` (safe) |
| `.../BankTransferService.java` | Transactions: `setAutoCommit(false)`, `commit()`, `rollback()` |
| `.../JdbcDemoApp.java` | Main class that runs every step with clear output |

## How to run
```
cd "D:\Java-SpringBoot\19-JDBC-Basics"
mvn -q compile exec:java
```
The first run downloads H2 and the exec plugin from Maven Central (needs internet once).
