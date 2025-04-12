package com.example.walletrest.presentation.exception.handler;

import java.time.Instant;

public record ApiError(
        int status,
        String message,
        Instant timestamp
) {
    public static ApiError of(int status, String message) {
        return new ApiError(status, message, Instant.now());
    }
}