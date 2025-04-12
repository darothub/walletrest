package com.example.walletrest.application.exception;

public class InvalidAssetPriceException extends RuntimeException {
    public InvalidAssetPriceException() {
        super("Invalid asset price.");
    }
}
