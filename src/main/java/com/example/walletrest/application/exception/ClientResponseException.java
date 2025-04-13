package com.example.walletrest.application.exception;

public class ClientResponseException extends RuntimeException {
    public ClientResponseException(String message) {
        super(message);
    }
}
