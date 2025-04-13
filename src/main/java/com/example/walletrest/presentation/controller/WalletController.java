package com.example.walletrest.presentation.controller;

import com.example.walletrest.application.service.WalletService;
import com.example.walletrest.domain.model.Wallet;
import com.example.walletrest.infrastructure.config.ApiConstants;
import com.example.walletrest.presentation.dto.AssetRequestDto;
import com.example.walletrest.presentation.dto.CreateWalletRequestDto;
import com.example.walletrest.presentation.dto.WalletResponseDto;
import com.example.walletrest.presentation.mapper.WalletMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.BASE_URL)
@Validated
@Tag(name = "Wallet", description = "Wallet REST API")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    @Operation(summary = "Create wallet")
    public ResponseEntity<WalletResponseDto> createWallet(@RequestBody @Valid CreateWalletRequestDto createWalletRequestDto) {
        Wallet wallet = walletService.createWallet(createWalletRequestDto.email());
        return ResponseEntity.ok(WalletMapper.toDto(wallet));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a wallet by id")
    public ResponseEntity<WalletResponseDto> getWallet(@PathVariable Long id) {
        Wallet wallet = walletService.getWallet(id);
        return ResponseEntity.ok(WalletMapper.toDto(wallet));
    }

    @PostMapping("/{id}/assets")
    @Operation(summary = "Add asset")
    public ResponseEntity<WalletResponseDto> addAssetToWallet(
            @PathVariable Long id,
            @Valid @RequestBody AssetRequestDto assetRequestDto
    ) {
        Wallet wallet = walletService.addAssetToWallet(id, assetRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(WalletMapper.toDto(wallet));
    }
}