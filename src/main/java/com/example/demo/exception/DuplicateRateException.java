package com.example.demo.exception;

public class DuplicateRateException extends RuntimeException {
    public DuplicateRateException(String message) {
        super(message);
    }
}
