package com.example.walletrest.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class Wallet {
    private Long id;
    private BigDecimal total;
    private List<Asset> assets;
}
