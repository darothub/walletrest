package com.example.walletrest.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CreateWalletRequestDto(
        @NotNull
        @Email(message = "Invalid email address")
        String email
) {
}
