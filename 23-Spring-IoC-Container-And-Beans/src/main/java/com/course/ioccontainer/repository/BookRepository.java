/*
 * @Repository - the stereotype for DATA ACCESS classes (talking to a database, file, API...).
 *
 * @Repository is a specialised @Component: component scanning finds it and creates a bean.
 * Extra bonus (used later with JPA/JDBC): Spring can translate low-level database
 * exceptions thrown from @Repository beans into Spring's DataAccessException family.
 *
 * Bean name: not given, so Spring uses the class name with a lower-case first letter:
 *            "bookRepository".
 *
 * For now the "database" is just an in-memory List.
 */
package com.course.ioccontainer.repository;

import com.course.ioccontainer.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {

    private final List<Book> books = new ArrayList<>();

    public BookRepository() {
        // This println lets you SEE when Spring creates the bean.
        System.out.println("   [container] creating BookRepository");
        books.add(new Book("978-0134685991", "Effective Java", "Joshua Bloch", true));
        books.add(new Book("978-1617294945", "Spring in Action", "Craig Walls", true));
        books.add(new Book("978-0596009205", "Head First Java", "Kathy Sierra", false));
    }

    public List<Book> findAll() {
        return List.copyOf(books);
    }

    public Optional<Book> findByIsbn(String isbn) {
        return books.stream().filter(b -> b.isbn().equals(isbn)).findFirst();
    }
}
