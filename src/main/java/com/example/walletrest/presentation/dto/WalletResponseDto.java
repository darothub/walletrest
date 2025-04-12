package com.example.walletrest.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WalletResponseDto {
    private Long id;
    private double total;
    private List<AssetResponseDto> assets;
}