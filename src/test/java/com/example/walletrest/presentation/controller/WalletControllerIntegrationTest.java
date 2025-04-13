package com.example.walletrest.presentation.controller;


import com.example.walletrest.application.service.WalletService;
import com.example.walletrest.domain.model.Asset;
import com.example.walletrest.domain.model.Wallet;
import com.example.walletrest.infrastructure.config.ApiConstants;
import com.example.walletrest.presentation.dto.AssetRequestDto;
import com.example.walletrest.presentation.dto.CreateWalletRequestDto;
import com.example.walletrest.presentation.dto.WalletResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
@ExtendWith(MockitoExtension.class)
public class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WalletService walletService;

    @Test
    void createWallet_shouldReturnCreatedWallet() throws Exception {
        String email = "test@example.com";
        CreateWalletRequestDto request = new CreateWalletRequestDto(email);
        Wallet wallet = new Wallet(1L, BigDecimal.ZERO, Collections.emptyList());

        given(walletService.createWallet(email)).willReturn(wallet);

        mockMvc.perform(post(ApiConstants.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.assets").isArray())
                .andExpect(jsonPath("$.assets").isEmpty());
    }

    @Test
    void createWallet_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        CreateWalletRequestDto request = new CreateWalletRequestDto("invalid-email");

        mockMvc.perform(post(ApiConstants.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWallet_shouldReturnWallet() throws Exception {
        Long walletId = 1L;
        String email = "test@example.com";
        Wallet wallet = new Wallet(walletId, BigDecimal.ZERO, Collections.emptyList());

        given(walletService.getWallet(walletId)).willReturn(wallet);

        mockMvc.perform(get(ApiConstants.BASE_URL + "/{id}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(walletId))
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.assets").isArray())
                .andExpect(jsonPath("$.assets").isEmpty());
    }


    @Test
    void addAssetToWallet_shouldReturnUpdatedWallet() throws Exception {
        Long walletId = 1L;
        String email = "test@example.com";
        AssetRequestDto assetRequest = new AssetRequestDto(
                "BTC",
                new BigDecimal("50000.00"),
                new BigDecimal("1"),
                new BigDecimal("50000.00")
        );

        Wallet wallet = new Wallet(walletId, BigDecimal.ZERO, Collections.emptyList());
        Wallet updatedWallet = new Wallet(walletId, new BigDecimal("50000.00"), List.of(
                new Asset("BTC",new BigDecimal("1"),new BigDecimal("50000.00"),  new BigDecimal("50000.00"))
        ));

        given(walletService.addAssetToWallet(walletId, assetRequest)).willReturn(updatedWallet);

        mockMvc.perform(post(ApiConstants.BASE_URL + "/{id}/assets", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assetRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(walletId))
                .andExpect(jsonPath("$.assets[0].symbol").value("BTC"))
                .andExpect(jsonPath("$.assets[0].quantity").value(1));
    }

    @Test
    void addAssetToWallet_withInvalidAsset_shouldReturnBadRequest() throws Exception {
        Long walletId = 1L;
        AssetRequestDto invalidAsset = new AssetRequestDto(
                "", // invalid symbol
                new BigDecimal("-100"), // invalid price
                new BigDecimal("-1"), // invalid quantity
                null // missing value
        );


        mockMvc.perform(post(ApiConstants.BASE_URL + "/{id}/assets", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAsset)))
                .andExpect(status().isBadRequest());
    }
}
