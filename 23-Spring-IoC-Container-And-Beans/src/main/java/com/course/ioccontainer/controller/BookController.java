/*
 * @Controller - the stereotype for the PRESENTATION / ENTRY layer.
 *
 * In a web application (Spring MVC, Topic 31) a @Controller receives HTTP requests.
 * We have no web server yet, so this controller is called from main() and prints text
 * to the console. The important part for now: it is a bean, created by component
 * scanning, and it receives BookService by constructor injection.
 */
package com.course.ioccontainer.controller;

import com.course.ioccontainer.service.BookService;
import org.springframework.stereotype.Controller;

@Controller
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        System.out.println("   [container] creating BookController");
        this.bookService = bookService;
    }

    public void showAvailableBooks() {
        System.out.println("   Available books:");
        bookService.availableBooks()
                .forEach(b -> System.out.println("     * " + b.title() + " by " + b.author()));
    }

    public void borrow(String isbn) {
        System.out.println("   " + bookService.borrow(isbn));
    }

    public void showLateFee(long daysLate) {
        System.out.println("   Late fee for " + daysLate + " days: " + bookService.lateFee(daysLate));
    }
}
