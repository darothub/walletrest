package com.example.walletrest.application.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Long id) {
        super("Wallet not found: " + id);
    }
}
