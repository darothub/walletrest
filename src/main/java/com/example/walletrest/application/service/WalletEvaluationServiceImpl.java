package com.example.walletrest.application.service;

import com.example.walletrest.application.exception.InvalidDateFormatException;
import com.example.walletrest.infrastructure.client.CoinCapClient;
import com.example.walletrest.presentation.dto.AssetRequestDto;
import com.example.walletrest.presentation.dto.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
@Service
public class WalletEvaluationServiceImpl implements WalletEvaluationService {
    private static final BigDecimal LOWEST_PERFORMANCE = new BigDecimal("-1000000000");
    private static final BigDecimal HIGHEST_PERFORMANCE = new BigDecimal("1000000000");
    private final CoinCapClient coinCapClient;

    @Override
    public Map<String, Object> evaluate(List<AssetRequestDto> assets, String date) {
        if(date != null) {
            date = resolveEvaluationDate(date).toString();
        }
        AtomicReference<BigDecimal> totalValue = new AtomicReference<>(BigDecimal.ZERO);
        String bestAsset = null, worstAsset = null;
        BigDecimal bestPerformance = LOWEST_PERFORMANCE;
        BigDecimal worstPerformance = HIGHEST_PERFORMANCE;

        for (AssetRequestDto asset : assets) {
            String symbol = asset.symbol();
            BigDecimal quantity = asset.quantity();
            BigDecimal originalValue = asset.value();
            BigDecimal originalUnitPrice = originalValue.divide(quantity, 3, RoundingMode.HALF_UP);
            TokenResponseDto tokenResponseDto = Objects.requireNonNull(coinCapClient.getTokensPrices().block()).get(symbol);
            if (tokenResponseDto == null) {
                throw new RuntimeException("Cannot find token for symbol: " + symbol);
            }
            String slug = tokenResponseDto.name();
            log.info("Slug: {}, {}", slug, date);
            BigDecimal latestPrice = coinCapClient.getHistoricalPrice(slug, date).block();

            if (latestPrice == null || latestPrice.compareTo(BigDecimal.ZERO) == 0) {
                 log.warn("Skipping {}: no price data", symbol);
            };

            BigDecimal performance = latestPrice.subtract(originalUnitPrice)
                    .divide(originalUnitPrice, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            BigDecimal newValue = latestPrice.multiply(quantity);
            totalValue.updateAndGet(v -> v.add(newValue));

            if (performance.compareTo(bestPerformance) > 0) {
                bestPerformance = performance;
                bestAsset = symbol;
            }

            if (performance.compareTo(worstPerformance) < 0) {
                worstPerformance = performance;
                worstAsset = symbol;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", totalValue.get().setScale(2, RoundingMode.HALF_UP));
        result.put("best_asset", bestAsset);
        result.put("best_performance", bestPerformance.setScale(2, RoundingMode.HALF_UP));
        result.put("worst_asset", worstAsset);
        result.put("worst_performance", worstPerformance.setScale(2, RoundingMode.HALF_UP));

        return result;
    }

    private LocalDate resolveEvaluationDate(String inputDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            LocalDate parsedDate = LocalDate.parse(inputDate, formatter);

            if (parsedDate.isAfter(LocalDate.now())) {
                throw new InvalidDateFormatException("Date cannot be in the future.");
            }

            return parsedDate;

        } catch (DateTimeParseException ex) {
            throw new InvalidDateFormatException("Expected dd/MM/yyyy");
        }
    }
}
