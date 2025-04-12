package com.example.walletrest.application.service;

import com.example.walletrest.presentation.dto.AssetRequestDto;

import java.util.List;
import java.util.Map;

public interface WalletEvaluationService {
    Map<String, Object> evaluate(List<AssetRequestDto> assets, String date);
}