package com.course.composeapp.book;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * BookRepository.java - Spring Data JPA writes the SQL for us (topic 38).
 * The same interface works against H2 (laptop) and MySQL (Compose).
 */
public interface BookRepository extends JpaRepository<Book, Long> {
}
