/*
 * Example07_GenericRepository.java
 *
 * What this file demonstrates - a GENERIC INTERFACE, the way Spring Data uses it:
 *   interface Repository<T, ID> {
 *       T save(T entity);
 *       Optional<T> findById(ID id);
 *       List<T> findAll();
 *       boolean existsById(ID id);
 *       void deleteById(ID id);
 *       long count();
 *   }
 *   - ONE in-memory implementation (InMemoryRepository<T extends Entity<ID>, ID>) works for
 *     Students (ID = Integer) AND Books (ID = String). Zero copy-paste.
 *   - A bounded type (T extends Entity<ID>) so the repository can call getId().
 *   - Extending a generic interface with a specific type: StudentRepository extends Repository<Student, Integer>
 *   - Optional<T> = "a box that may be empty" - findById returns it instead of null.
 *     (Full story of Optional in Topic 14.)
 *
 * In Spring Data JPA (Topic 38) you will write just:
 *     interface StudentRepository extends JpaRepository<Student, Long> { }
 * and Spring generates the implementation for you. This file shows the idea behind it.
 *
 * How to run:
 *   java Example07_GenericRepository.java
 *
 * Output:
 *   Saved students: 3
 *   Find 2: Student{id=2, name=Ravi, year=3}
 *   Find 9 present? false
 *   Exists 1? true
 *   All students: [Student{id=1, name=Asha, year=2}, Student{id=2, name=Ravi, year=3}, Student{id=3, name=Meena, year=2}]
 *   Second-year students: [Student{id=1, name=Asha, year=2}, Student{id=3, name=Meena, year=2}]
 *   After deleteById(1): count=2
 *   Book ISBN-111: Book{isbn=ISBN-111, title=Clean Code}
 *   Missing book -> default title: Not in library
 *   Books: 2
 */
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Example07_GenericRepository {

    public static void main(String[] args) {
        // ---------- Students: T = Student, ID = Integer ----------
        StudentRepository students = new InMemoryStudentRepository();
        students.save(new Student(1, "Asha", 2));
        students.save(new Student(2, "Ravi", 3));
        students.save(new Student(3, "Meena", 2));
        System.out.println("Saved students: " + students.count());

        Optional<Student> found = students.findById(2);
        if (found.isPresent()) {
            System.out.println("Find 2: " + found.get());
        }
        System.out.println("Find 9 present? " + students.findById(9).isPresent());
        System.out.println("Exists 1? " + students.existsById(1));
        System.out.println("All students: " + students.findAll());
        System.out.println("Second-year students: " + students.findByYear(2));   // custom method

        students.deleteById(1);
        System.out.println("After deleteById(1): count=" + students.count());

        // ---------- Books: T = Book, ID = String - SAME generic code, no copy-paste ----------
        Repository<Book, String> books = new InMemoryRepository<>();
        books.save(new Book("ISBN-111", "Clean Code"));
        books.save(new Book("ISBN-222", "Effective Java"));
        System.out.println("Book ISBN-111: " + books.findById("ISBN-111").orElseThrow());
        String title = books.findById("ISBN-999").map(Book::getTitle).orElse("Not in library");
        System.out.println("Missing book -> default title: " + title);
        System.out.println("Books: " + books.count());
        // books.findById(5);   // compile error: the ID type of this repository is String, not Integer
    }
}

// ---------- 1. Every entity knows its own id ----------
interface Entity<ID> {
    ID getId();
}

// ---------- 2. The generic repository contract (like Spring Data's CrudRepository) ----------
interface Repository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean existsById(ID id);
    void deleteById(ID id);
    long count();
}

// ---------- 3. ONE implementation for ALL entity types ----------
// T must be an Entity whose id type is ID - so we may call entity.getId().
class InMemoryRepository<T extends Entity<ID>, ID> implements Repository<T, ID> {

    protected final Map<ID, T> store = new LinkedHashMap<>();   // keeps insertion order

    @Override
    public T save(T entity) {
        store.put(entity.getId(), entity);   // insert or replace
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(store.get(id));   // empty Optional instead of null
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean existsById(ID id) {
        return store.containsKey(id);
    }

    @Override
    public void deleteById(ID id) {
        store.remove(id);
    }

    @Override
    public long count() {
        return store.size();
    }
}

// ---------- 4. A specific repository adds its own query methods ----------
interface StudentRepository extends Repository<Student, Integer> {
    List<Student> findByYear(int year);      // Spring Data would generate this from the method name!
}

class InMemoryStudentRepository extends InMemoryRepository<Student, Integer> implements StudentRepository {
    @Override
    public List<Student> findByYear(int year) {
        List<Student> result = new ArrayList<>();
        for (Student s : store.values()) {
            if (s.getYear() == year) {
                result.add(s);
            }
        }
        return result;
    }
}

// ---------- Entities ----------
class Student implements Entity<Integer> {
    private final Integer id;
    private final String name;
    private final int year;

    Student(Integer id, String name, int year) {
        this.id = id;
        this.name = name;
        this.year = year;
    }

    @Override
    public Integer getId() { return id; }
    int getYear() { return year; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name=" + name + ", year=" + year + "}";
    }
}

class Book implements Entity<String> {
    private final String isbn;
    private final String title;

    Book(String isbn, String title) {
        this.isbn = isbn;
        this.title = title;
    }

    @Override
    public String getId() { return isbn; }
    String getTitle() { return title; }

    @Override
    public String toString() {
        return "Book{isbn=" + isbn + ", title=" + title + "}";
    }
}
