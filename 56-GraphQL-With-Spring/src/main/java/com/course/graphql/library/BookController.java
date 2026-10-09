package com.course.graphql.library;

/*
 * The GraphQL "controller". There are NO URLs here!
 * Each method is a resolver for one FIELD of the schema:
 *
 *   @QueryMapping      -> a field of "type Query"     (method name = field name)
 *   @MutationMapping   -> a field of "type Mutation"
 *   @SchemaMapping     -> a field of any other type   (e.g. Author.books)
 *   @BatchMapping      -> like @SchemaMapping, but loads the field for MANY parents at once (fixes N+1)
 *   @Argument          -> reads a GraphQL argument, e.g. bookById(id: 1)
 *
 * The @Controller annotation (not @RestController!) marks this class for Spring for GraphQL.
 */
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    private final LibraryRepository repository;

    public BookController(LibraryRepository repository) {
        this.repository = repository;
    }

    // ===================== Queries =====================

    // schema: books: [Book!]!
    @QueryMapping
    public List<Book> books() {
        return repository.findAllBooks();
    }

    // schema: bookById(id: ID!): Book      -> returning null is allowed (Book has no "!")
    @QueryMapping
    public Book bookById(@Argument Long id) {
        return repository.findBookById(id).orElse(null);
    }

    // schema: searchBooks(title: String!): [Book!]!
    @QueryMapping
    public List<Book> searchBooks(@Argument String title) {
        return repository.findBooksByTitle(title);
    }

    @QueryMapping
    public List<Author> authors() {
        return repository.findAllAuthors();
    }

    @QueryMapping
    public Author authorById(@Argument Long id) {
        return repository.findAuthorById(id).orElse(null);
    }

    // ===================== Mutations =====================

    // schema: addBook(input: BookInput!): Book!
    @MutationMapping
    public Book addBook(@Argument BookInput input) {
        if (input.pages() <= 0) {
            throw new IllegalArgumentException("pages must be greater than 0");
        }
        repository.findAuthorById(input.authorId())
                .orElseThrow(() -> new BookNotFoundException("Author " + input.authorId() + " does not exist"));
        return repository.save(new Book(null, input.title(), input.pages(), input.authorId()));
    }

    // schema: deleteBook(id: ID!): Boolean!
    @MutationMapping
    public boolean deleteBook(@Argument Long id) {
        if (!repository.deleteBook(id)) {
            throw new BookNotFoundException("Book " + id + " does not exist");
        }
        return true;
    }

    // ===================== Nested fields =====================

    /*
     * Book.author - the N+1 PROBLEM and its FIX.
     *
     * The naive way would be:
     *
     *   @SchemaMapping(typeName = "Book", field = "author")
     *   public Author author(Book book) { return repository.findAuthorById(book.authorId()).orElseThrow(); }
     *
     * For a query returning 100 books, that runs 1 query for the books + 100 author queries = N+1!
     *
     * @BatchMapping receives ALL the books of the current response at once,
     * so we can load all their authors with ONE query and return a Map<Book, Author>.
     */
    @BatchMapping
    public Map<Book, Author> author(List<Book> books) {
        List<Long> authorIds = books.stream().map(Book::authorId).toList();
        log.debug("@BatchMapping: loading authors {} for {} books in ONE call", authorIds, books.size());

        Map<Long, Author> authorsById = repository.findAuthorsByIds(authorIds).stream()
                .collect(Collectors.toMap(Author::id, Function.identity()));

        return books.stream().collect(Collectors.toMap(Function.identity(), b -> authorsById.get(b.authorId())));
    }

    // Author.books - the parent Author object is passed in as the "source".
    @SchemaMapping(typeName = "Author", field = "books")
    public List<Book> books(Author author) {
        return repository.findBooksByAuthorId(author.id());
    }
}
