package com.example.walletrest.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AssetRequestDto(
        @NotBlank(message = "Token symbol must not be blank")
        String symbol,
        @Min(value = 0, message = "Price must be positive")
        BigDecimal price,
        @Min(value = 0, message = "Quantity must be zero or positive")
        BigDecimal quantity,
        BigDecimal value
) { }
