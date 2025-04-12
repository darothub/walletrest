package com.example.walletrest.presentation.mapper;


import com.example.walletrest.infrastructure.entity.Asset;
import com.example.walletrest.infrastructure.entity.Wallet;
import com.example.walletrest.presentation.dto.AssetResponseDto;
import com.example.walletrest.presentation.dto.WalletResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


public class WalletMapper {

    public static AssetResponseDto toDto(Asset asset) {
        return new AssetResponseDto(
                asset.getSymbol(),
                asset.getQuantity(),
                asset.getPrice(),
                asset.getPrice().multiply(asset.getQuantity())
        );
    }

    public static WalletResponseDto toDto(Wallet wallet) {
        List<AssetResponseDto> assetDtos = wallet.getAssets().stream()
                .map(WalletMapper::toDto)
                .toList();
        var totalAssetValue = assetDtos.stream().map(AssetResponseDto::value).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new WalletResponseDto(wallet.getId(), totalAssetValue, assetDtos);
    }
}
