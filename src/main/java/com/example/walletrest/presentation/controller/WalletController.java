package com.example.walletrest.presentation.controller;

import com.example.walletrest.application.service.WalletService;
import com.example.walletrest.domain.model.Wallet;
import com.example.walletrest.infrastructure.config.ApiConstants;
import com.example.walletrest.presentation.dto.AssetRequestDto;
import com.example.walletrest.presentation.dto.CreateWalletRequestDto;
import com.example.walletrest.presentation.dto.WalletResponseDto;
import com.example.walletrest.presentation.mapper.WalletMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.BASE_URL)
@Validated
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponseDto> createWallet(@RequestBody @Valid CreateWalletRequestDto createWalletRequestDto) {
        Wallet wallet = walletService.createWallet(createWalletRequestDto.email());
        return ResponseEntity.ok(WalletMapper.toDto(wallet));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponseDto> getWallet(@PathVariable Long id) {
        Wallet wallet = walletService.getWallet(id);
        return ResponseEntity.ok(WalletMapper.toDto(wallet));
    }

    @PostMapping("/{id}/assets")
    public ResponseEntity<WalletResponseDto> addAssetToWallet(
            @PathVariable Long id,
            @Valid @RequestBody AssetRequestDto assetRequestDto
    ) {
        Wallet wallet = walletService.addAssetToWallet(id, assetRequestDto);
        return ResponseEntity.ok(WalletMapper.toDto(wallet));
    }
}