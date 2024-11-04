package com.formation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    private final String field;
    private final transient Object value;

    public BadRequestException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }

    public BadRequestException(String message, String field, Object value) {
        super(message);
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}