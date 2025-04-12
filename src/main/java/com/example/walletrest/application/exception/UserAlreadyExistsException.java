package com.example.walletrest.application.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("Wallet with " + email + " already exists");
    }
}
