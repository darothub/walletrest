package com.example.walletrest.presentation.mapper;


import com.example.walletrest.domain.model.Asset;
import com.example.walletrest.domain.model.Wallet;
import com.example.walletrest.infrastructure.entity.AssetEntity;
import com.example.walletrest.infrastructure.entity.WalletEntity;
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

        return new WalletResponseDto(wallet.getId(), wallet.getTotal(), assetDtos);
    }

    public static AssetEntity toEntity(AssetRequestDto dto, BigDecimal validatedPrice, WalletEntity walletEntity) {
        AssetEntity assetEntity = new AssetEntity();
        assetEntity.setSymbol(dto.symbol().toUpperCase());
        assetEntity.setQuantity(dto.quantity());
        assetEntity.setPrice(validatedPrice);
        assetEntity.setWallet(walletEntity);
        return assetEntity;
    }

    public static Asset toDomain(AssetEntity assetEntity) {
        return new Asset(
                assetEntity.getSymbol(),
                assetEntity.getQuantity(),
                assetEntity.getPrice(),
                assetEntity.getPrice().multiply(assetEntity.getQuantity())
        );
    }

    public static Wallet toDomain(WalletEntity walletEntity) {
        var assets = walletEntity.getAssetEntities().stream().map(WalletMapper::toDomain).toList();
        var totalAssetValue = assets. stream().map(
                    a -> a.getPrice().multiply(a.getQuantity())
                ).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Wallet(
                walletEntity.getId(),
                totalAssetValue.setScale(2, BigDecimal.ROUND_HALF_UP),
                assets
        );
    }

}
