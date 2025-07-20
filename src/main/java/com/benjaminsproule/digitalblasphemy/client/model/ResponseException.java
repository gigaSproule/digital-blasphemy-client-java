package com.benjaminsproule.digitalblasphemy.client.model;

import java.util.List;

public class ResponseException extends Exception {
    private final int code;
    private final String description;
    private final List<String> errors;

    public ResponseException(ResponseError responseError) {
        this(responseError.code(), responseError.description(), responseError.errors());
    }

    public ResponseException(int code, String description) {
        this(code, description, null);
    }

    public ResponseException(int code, String description, List<String> errors) {
        this.code = code;
        this.description = description;
        this.errors = errors;
    }

    public String toString() {
        return "Code: %d, Description: %s, Errors: %s".formatted(code, description, errors);
    }
}
