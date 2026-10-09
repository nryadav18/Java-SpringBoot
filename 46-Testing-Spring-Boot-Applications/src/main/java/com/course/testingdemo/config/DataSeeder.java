package com.course.testingdemo.config;

import com.course.testingdemo.book.Book;
import com.course.testingdemo.book.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/*
 * Sample books for when YOU run the app (mvn spring-boot:run).
 *
 * @ConditionalOnProperty: this bean exists only when app.seed-data=true.
 * application.properties sets it to true; the integration test switches it OFF
 * (properties = "app.seed-data=false") so every test starts from a known, empty table.
 * Tests must not depend on random sample data!
 */
@Component
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final BookRepository repository;

    public DataSeeder(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            repository.saveAll(List.of(
                    new Book("Clean Code", "Robert C. Martin", "978-0132350884", new BigDecimal("450.00")),
                    new Book("Effective Java", "Joshua Bloch", "978-0134685991", new BigDecimal("599.00")),
                    new Book("Head First Java", "Kathy Sierra", "978-1491910771", new BigDecimal("520.50"))));
        }
    }
}
