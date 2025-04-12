package com.example.walletrest.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class WalletResponseDto {
    private Long id;
    private BigDecimal total;
    private List<AssetResponseDto> assets;
}