package com.katibaai.backend.auth.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class GraphQlExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof IllegalArgumentException) {
            return GraphqlErrorBuilder.newError(env)
                    .message(ex.getMessage())
                    .errorType(ErrorType.NOT_FOUND)
                    .build();
        }

        if (ex instanceof IllegalStateException) {
            return GraphqlErrorBuilder.newError(env)
                    .message(ex.getMessage())
                    .errorType(ErrorType.BAD_REQUEST)
                    .build();
        }

        if (ex instanceof jakarta.validation.ConstraintViolationException violationEx) {
            String message = violationEx.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .findFirst()
                    .orElse("Invalid input");

            return GraphqlErrorBuilder.newError(env)
                    .message(message)
                    .errorType(ErrorType.BAD_REQUEST)
                    .build();
        }

        return GraphqlErrorBuilder.newError(env)
                .message("An unexpected error occurred while processing your request.")
                .errorType(ErrorType.INTERNAL_ERROR)
                .build();
    }
}