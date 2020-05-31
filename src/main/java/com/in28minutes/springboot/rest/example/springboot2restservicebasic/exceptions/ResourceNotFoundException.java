package com.in28minutes.springboot.rest.example.springboot2restservicebasic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение: Ресурс не найден (404, Not Found)
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(Class<?> clazz, Long id) {
        super(String.format("%s is not found for this id :: %s", clazz.getSimpleName(), id));
    }
}