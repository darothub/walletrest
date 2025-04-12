package com.example.walletrest.application.service;

import com.example.walletrest.domain.model.Wallet;
import com.example.walletrest.presentation.dto.AssetRequestDto;

public interface WalletService {
    Wallet createWallet(String email);
    Wallet getWallet(Long id);
    Wallet addAssetToWallet(Long walletId, AssetRequestDto dto);
}