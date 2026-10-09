package com.course.restbasics;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/*
 * The REST controller for the "books" resource.
 *
 * @RestController  = @Controller + @ResponseBody on every method:
 *                    return values are written into the HTTP response body as JSON (via Jackson),
 *                    NOT treated as the name of an HTML page.
 * @RequestMapping("/api/books") = a common URL prefix for every method in this class.
 *
 * Endpoint summary:
 *   GET    /api/books                 -> all books                   200 OK
 *   GET    /api/books/{id}            -> one book                    200 OK / 404 Not Found
 *   GET    /api/books/search?author=x -> books whose author matches  200 OK
 *   POST   /api/books                 -> create a book (JSON body)   201 Created
 *   DELETE /api/books/{id}            -> delete a book               204 No Content / 404
 *
 * Topic 32 goes deeper into @PathVariable, @RequestParam, @RequestBody and ResponseEntity.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // Constructor injection: Spring gives us the BookService bean.
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // GET /api/books  ->  [ {...}, {...} ]   (a List becomes a JSON array)
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }

    // GET /api/books/2  ->  {"id":2,...}
    // {id} is a placeholder in the URL; @PathVariable copies it into the "id" parameter.
    @GetMapping("/{id}")
    public Book getBook(@PathVariable long id) {
        return bookService.findById(id)
                // No book? Answer with HTTP 404 instead of 200 with an empty body.
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book " + id + " not found"));
    }

    // GET /api/books/search?author=bloch
    // @RequestParam reads "?author=..." from the query string.
    @GetMapping("/search")
    public List<Book> searchByAuthor(@RequestParam String author) {
        return bookService.findByAuthor(author);
    }

    // POST /api/books   with JSON body {"title":"...","author":"...","year":2020,"price":300}
    // @RequestBody tells Spring: "read the JSON body and turn it into a Book" (Jackson again).
    // @ResponseStatus(CREATED) makes the answer 201 instead of the default 200.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book addBook(@RequestBody Book book) {
        return bookService.add(book);
    }

    // DELETE /api/books/3  -> 204 No Content (success, nothing to send back)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable long id) {
        if (!bookService.deleteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book " + id + " not found");
        }
    }
}
