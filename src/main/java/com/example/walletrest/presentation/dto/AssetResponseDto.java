package com.example.walletrest.presentation.dto;

import java.math.BigDecimal;

public record AssetResponseDto(
        String symbol,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal value
) {}
