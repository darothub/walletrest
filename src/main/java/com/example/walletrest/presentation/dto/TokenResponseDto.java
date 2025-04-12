package com.example.walletrest.presentation.dto;


import java.math.BigDecimal;

public record TokenResponseDto(
        String name,
        BigDecimal price
){}
