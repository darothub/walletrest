package com.example.walletrest.presentation.client;

import com.example.walletrest.presentation.config.CoinCapProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CoinCapClient {

    private final WebClient webClient;

    public CoinCapClient(WebClient.Builder builder, CoinCapProperties properties) {
        log.info("Creating CoinCapClient with {}", properties);
        this.webClient = builder
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + properties.getApiKey())
                .build();
    }

    public Mono<Map<String, BigDecimal>> getTokensPrices() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/assets")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {

                    Map<String, BigDecimal> result = new HashMap<>();
                    Object dataObj = response.get("data");

                    if (dataObj instanceof List<?> dataList) {
                        for (Object obj : dataList) {
                            Map<String, Object> entry = (Map<String, Object>) obj;
                            String symbol = ((String) entry.get("symbol")).toUpperCase();
                            BigDecimal price = new BigDecimal((String) entry.get("priceUsd")).setScale(2, RoundingMode.HALF_UP);
                            result.put(symbol, price);
                            log.info(symbol + " - $" + price);
                        }
                    }
                    return result;
                });
    }
    public Mono<BigDecimal> getHistoricalPrice(String symbol, String date) {
        Instant start = LocalDate.parse(date).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = start.plusSeconds(86399);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/assets/{symbol}/history")
                        .queryParam("interval", "d1")
                        .queryParam("start", start.toEpochMilli())
                        .queryParam("end", end.toEpochMilli())
                        .build(symbol.toLowerCase()))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    Object dataObj = response.get("data");

                    if (dataObj instanceof List<?> list && !list.isEmpty()) {
                        Map<String, Object> entry = (Map<String, Object>) list.get(0);
                        String priceStr = entry.get("priceUsd").toString();
                        return new BigDecimal(priceStr);
                    }

                    return BigDecimal.ZERO;
                });
    }
}