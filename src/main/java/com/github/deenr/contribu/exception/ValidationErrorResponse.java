package com.github.deenr.contribu.exception;

import java.util.Map;

public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> errors;

    public ValidationErrorResponse(String message, int status, Map<String, String> errors) {
        super(message, status);
        this.errors = errors;
    }

    // Getter
    public Map<String, String> getErrors() {
        return errors;
    }
}