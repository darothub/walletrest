package com.example.walletrest.application.exception;

public class InvalidDateFormatException extends RuntimeException {
    public InvalidDateFormatException(String message) {
        super("Invalid date format: " + message);
    }
}
