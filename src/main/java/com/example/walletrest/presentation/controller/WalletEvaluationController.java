package com.example.walletrest.presentation.controller;

import com.example.walletrest.application.service.WalletEvaluationService;
import com.example.walletrest.infrastructure.config.ApiConstants;
import com.example.walletrest.presentation.dto.WalletEvaluationRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(ApiConstants.BASE_URL)
@Tag(name = "Wallet", description = "Wallet REST API")
public class WalletEvaluationController {

    private final WalletEvaluationService walletEvaluationService;

    public WalletEvaluationController(WalletEvaluationService walletEvaluationService) {
        this.walletEvaluationService = walletEvaluationService;
    }

    @PostMapping("/evaluate")
    @Operation(summary = "Evaluate wallet")
    public ResponseEntity<Map<String, Object>> evaluateWallet(
            @Valid @RequestBody WalletEvaluationRequestDto request,
            @RequestParam(required = false) String date
    ) {
        Map<String, Object> result = walletEvaluationService.evaluate(request.assets(), date);
        return ResponseEntity.ok(result);
    }
}