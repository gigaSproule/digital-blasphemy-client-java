package com.benjaminsproule.digitalblasphemy.client.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class ResponseExceptionTest {

    @Nested
    class ResponseErrorArgumentConstructor {
        @Test
        void testGetMessageFullyPopulated() {
            ResponseException responseException = new ResponseException(new ResponseError(1, "description", List.of("Error1", "Error2")));
            assertThat(responseException.getMessage()).isEqualTo("Code: 1, Description: description, Errors: [Error1, Error2]");
        }

        @Test
        void testGetMessageNullErrors() {
            ResponseException responseException = new ResponseException(new ResponseError(1, "description", null));
            assertThat(responseException.getMessage()).isEqualTo("Code: 1, Description: description, Errors: []");
        }

        @Test
        void testGetMessageEmptyListOfErrors() {
            ResponseException responseException = new ResponseException(new ResponseError(1, "description", emptyList()));
            assertThat(responseException.getMessage()).isEqualTo("Code: 1, Description: description, Errors: []");
        }
    }

    @Nested
    class SeparateArgumentsConstructor {
        @Test
        void testGetMessageFullyPopulated() {
            ResponseException responseException = new ResponseException(1, "description", List.of("Error1", "Error2"));
            assertThat(responseException.getMessage()).isEqualTo("Code: 1, Description: description, Errors: [Error1, Error2]");
        }

        @Test
        void testGetMessageNullErrors() {
            ResponseException responseException = new ResponseException(1, "description", null);
            assertThat(responseException.getMessage()).isEqualTo("Code: 1, Description: description, Errors: []");
        }

        @Test
        void testGetMessageEmptyListOfErrors() {
            ResponseException responseException = new ResponseException(1, "description", emptyList());
            assertThat(responseException.getMessage()).isEqualTo("Code: 1, Description: description, Errors: []");
        }
    }

}
