package com.example.walletrest.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Asset {
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal value;
}
