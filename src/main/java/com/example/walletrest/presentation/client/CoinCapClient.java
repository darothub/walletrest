package com.example.walletrest.presentation.client;

import com.example.walletrest.presentation.config.CoinCapProperties;
import com.example.walletrest.presentation.dto.TokenResponseDto;
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
    public Mono<Map<String, TokenResponseDto>> getTokensPrices() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/assets")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {

                    Map<String, TokenResponseDto> result = new HashMap<>();
                    Object dataObj = response.get("data");

                    if (dataObj instanceof List<?> dataList) {
                        for (Object obj : dataList) {
                            Map<String, Object> entry = (Map<String, Object>) obj;
                            String symbol = ((String) entry.get("symbol")).toUpperCase();
                            String name = ((String) entry.get("name")).toLowerCase();
                            BigDecimal price = new BigDecimal((String) entry.get("priceUsd")).setScale(2, RoundingMode.HALF_UP);
                            TokenResponseDto tokenResponseDto = new TokenResponseDto(name, price);
                            result.put(symbol, tokenResponseDto);
                            log.info(symbol + " -" + tokenResponseDto);
                        }
                    }
                    return result;
                });
    }
    public Mono<BigDecimal> getHistoricalPrice(String slug, String date) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/assets/{slug}/history")
                            .queryParam("interval", "d1");

                    if (date != null && !date.isBlank()) {
                        Instant startInstant = LocalDate.parse(date).atStartOfDay(ZoneOffset.UTC).toInstant();
                        long start = startInstant.toEpochMilli();
                        long end = startInstant.plusSeconds(86399).toEpochMilli();

                        uriBuilder.queryParam("start", start)
                                .queryParam("end", end);
                    }

                    return uriBuilder.build(slug.toLowerCase());
                })
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    Object dataObj = response.get("data");

                    if (dataObj instanceof List<?> list && !list.isEmpty()) {
                        Map<String, Object> entry = (Map<String, Object>) list.getLast();
                        String priceStr = entry.get("priceUsd").toString();
                        return new BigDecimal(priceStr);
                    }

                    return BigDecimal.ZERO;
                });
    }
}