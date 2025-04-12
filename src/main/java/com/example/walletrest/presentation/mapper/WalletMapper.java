package com.example.walletrest.presentation.mapper;


import com.example.walletrest.infrastructure.entity.Asset;
import com.example.walletrest.infrastructure.entity.Wallet;
import com.example.walletrest.presentation.dto.AssetRequestDto;
import com.example.walletrest.presentation.dto.AssetResponseDto;
import com.example.walletrest.presentation.dto.WalletResponseDto;

import java.math.BigDecimal;


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
        var assetDtos = wallet.getAssets().stream()
                .map(WalletMapper::toDto)
                .toList();
        var totalAssetValue = assetDtos.stream().map(AssetResponseDto::value).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new WalletResponseDto(wallet.getId(), totalAssetValue, assetDtos);
    }

    public static Asset toEntity(AssetRequestDto dto, BigDecimal validatedPrice, Wallet wallet) {
        Asset asset = new Asset();
        asset.setSymbol(dto.symbol().toUpperCase());
        asset.setQuantity(dto.quantity());
        asset.setPrice(validatedPrice);
        asset.setWallet(wallet);
        return asset;
    }
}
