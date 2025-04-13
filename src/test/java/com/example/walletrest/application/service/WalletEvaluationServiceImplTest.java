package com.example.walletrest.application.service;

import com.example.walletrest.application.exception.InvalidDateFormatException;
import com.example.walletrest.domain.model.Asset;

import com.example.walletrest.infrastructure.client.CoinCapClient;
import com.example.walletrest.presentation.dto.AssetRequestDto;
import com.example.walletrest.presentation.dto.TokenResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletEvaluationServiceImplTest {

    @Mock
    private CoinCapClient coinCapClient;

    @InjectMocks
    private WalletEvaluationServiceImpl walletEvaluationService;

    private final TokenResponseDto btcToken = new TokenResponseDto("bitcoin", new BigDecimal("50000.00"));
    private final TokenResponseDto ethToken = new TokenResponseDto("ethereum", new BigDecimal("3000.00"));
    private final TokenResponseDto solToken = new TokenResponseDto("solana", new BigDecimal("100.00"));


    @Test
    void evaluate_shouldCalculateCorrectValuesForMultipleAssets() {
        when(coinCapClient.getTokensPrices())
                .thenReturn(Mono.just(Map.of(
                        "BTC", btcToken,
                        "ETH", ethToken,
                        "SOL", solToken
                )));
        List<AssetRequestDto> assets = List.of(
                new AssetRequestDto("BTC", null, new BigDecimal("1"), new BigDecimal("50000.00")),
                new AssetRequestDto("ETH", null, new BigDecimal("10"), new BigDecimal("30000.00"))
        );

        when(coinCapClient.getHistoricalPrice("bitcoin", null))
                .thenReturn(Mono.just(new BigDecimal("60000.00")));
        when(coinCapClient.getHistoricalPrice("ethereum", null))
                .thenReturn(Mono.just(new BigDecimal("3500.00")));

        Map<String, Object> result = walletEvaluationService.evaluate(assets, null);

        assertEquals(new BigDecimal("95000.00"), result.get("total"));
        assertEquals("BTC", result.get("best_asset"));
        assertEquals(new BigDecimal("20.00"), result.get("best_performance"));
        assertEquals("ETH", result.get("worst_asset"));
        assertEquals(new BigDecimal("16.67"), result.get("worst_performance"));
    }

    @Test
    void evaluate_shouldHandleHistoricalDateCorrectly() {
        when(coinCapClient.getTokensPrices())
                .thenReturn(Mono.just(Map.of(
                        "BTC", btcToken,
                        "ETH", ethToken,
                        "SOL", solToken
                )));
        List<AssetRequestDto> assets = List.of(
                new AssetRequestDto("SOL", null, new BigDecimal("100"), new BigDecimal("10000.00"))
        );

        when(coinCapClient.getHistoricalPrice("solana", "2022-01-01"))
                .thenReturn(Mono.just(new BigDecimal("150.00")));

        Map<String, Object> result = walletEvaluationService.evaluate(assets, "01/01/2022");

        assertEquals(new BigDecimal("15000.00"), result.get("total"));
        assertEquals("SOL", result.get("best_asset"));
        assertEquals(new BigDecimal("50.00"), result.get("best_performance"));
    }

    @Test
    void evaluate_shouldHandleZeroQuantityGracefully() {
        // Given
        List<AssetRequestDto> assets = List.of(
                new AssetRequestDto("BTC", null, BigDecimal.ZERO, BigDecimal.ZERO)
        );

        // When
        assertThrows(ArithmeticException.class, () -> walletEvaluationService.evaluate(assets, null));

        // Then

//    / /        assertEquals(BigDecimal.ZERO.setScale(2), result.get("total"));
    }

    @Test
    void evaluate_shouldHandleUnknownSymbols() {
        when(coinCapClient.getTokensPrices())
                .thenReturn(Mono.just(Map.of(
                        "BTC", btcToken,
                        "ETH", ethToken,
                        "SOL", solToken
                )));
        List<AssetRequestDto> assets = List.of(
                new AssetRequestDto("UNKNOWN", new BigDecimal("100.00"), new BigDecimal("10"), new BigDecimal("1000.00"))
        );


        assertThrows(RuntimeException.class, () -> walletEvaluationService.evaluate(assets, null));
    }

    @Test
    void evaluate_shouldHandleNegativePerformance() {
        when(coinCapClient.getTokensPrices())
                .thenReturn(Mono.just(Map.of(
                        "BTC", btcToken,
                        "ETH", ethToken,
                        "SOL", solToken
                )));
        List<AssetRequestDto> assets = List.of(
                new AssetRequestDto("ETH", new BigDecimal("3000.00"), new BigDecimal("5"), new BigDecimal("15000.00"))
        );

        when(coinCapClient.getHistoricalPrice("ethereum", null))
                .thenReturn(Mono.just(new BigDecimal("2500.00")));
        Map<String, Object> result = walletEvaluationService.evaluate(assets, null);

        assertEquals(new BigDecimal("12500.00"), result.get("total"));
        assertEquals(new BigDecimal("-16.67"), result.get("best_performance"));
    }

    @Test
    void evaluate_shouldHandleExtremePerformanceValues() {
        when(coinCapClient.getTokensPrices())
                .thenReturn(Mono.just(Map.of(
                        "BTC", btcToken,
                        "ETH", ethToken,
                        "SOL", solToken
                )));

        List<AssetRequestDto> assets = List.of(
                new AssetRequestDto("SOL", new BigDecimal("0.01"), new BigDecimal("1000"), new BigDecimal("10.00"))
        );

        when(coinCapClient.getHistoricalPrice("solana", null))
                .thenReturn(Mono.just(new BigDecimal("100.00")));

        Map<String, Object> result = walletEvaluationService.evaluate(assets, null);

        assertEquals(new BigDecimal("100000.00"), result.get("total"));
        assertEquals(new BigDecimal("999900.00"), result.get("best_performance"));
    }
}