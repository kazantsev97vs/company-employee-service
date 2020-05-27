package com.in28minutes.springboot.rest.example.springboot2restservicebasic.exceptions;

public class CompanyEmployeeNotFoundException extends Exception {
    public CompanyEmployeeNotFoundException(String message) {
        super(message);
    }
}
