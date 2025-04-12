package com.example.walletrest.presentation.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record WalletEvaluationRequestDto(
        @NotEmpty(message = "Request can not be empty")
        List<AssetRequestDto> assets
) { }
