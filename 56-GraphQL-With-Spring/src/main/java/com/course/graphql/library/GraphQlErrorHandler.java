package com.course.graphql.library;

/*
 * Error handling for GraphQL.
 *
 * Big difference from REST: a GraphQL response is (almost) always HTTP 200.
 * Errors are reported inside the JSON body, in an "errors" array:
 *
 *   { "data": { "deleteBook": null },
 *     "errors": [ { "message": "Book 99 does not exist",
 *                   "path": ["deleteBook"],
 *                   "extensions": { "classification": "NOT_FOUND" } } ] }
 *
 * Without this class, unknown exceptions become "INTERNAL_ERROR" with a generic message
 * (good: no stack traces leak to the client). Here we map our own exceptions to
 * clear messages and categories.
 *
 * @ControllerAdvice + @GraphQlExceptionHandler is the GraphQL twin of
 * @RestControllerAdvice + @ExceptionHandler (Topic 35).
 */
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

@ControllerAdvice
public class GraphQlErrorHandler {

    @GraphQlExceptionHandler
    public GraphQLError handleNotFound(BookNotFoundException ex, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env)          // fills in "path" and "locations"
                .errorType(ErrorType.NOT_FOUND)
                .message(ex.getMessage())
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleBadInput(IllegalArgumentException ex, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
    }
}
