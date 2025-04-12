package com.example.walletrest.infrastructure.scheduler;

import com.example.walletrest.infrastructure.client.CoinCapClient;
import com.example.walletrest.infrastructure.entity.AssetEntity;
import com.example.walletrest.infrastructure.repository.AssetRepository;
import com.example.walletrest.presentation.dto.TokenResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class PriceUpdateScheduler {

    private final AssetRepository assetRepository;
    private final CoinCapClient coinCapClient;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public PriceUpdateScheduler(AssetRepository assetRepository, CoinCapClient coinCapClient) {
        this.assetRepository = assetRepository;
        this.coinCapClient = coinCapClient;
    }

    @Scheduled(fixedDelayString = "${wallet.price.update.interval}")
    public void updatePrices() {
        List<AssetEntity> assets = assetRepository.findAll();
        if (!assets.isEmpty()) {
            Map<String, TokenResponseDto> tokenPrices = fetchTokenPrices();

            List<Callable<Void>> tasks = assets.stream()
                    .map(asset -> createUpdateTask(asset, tokenPrices))
                    .toList();

            executeTasks(tasks);
        }
    }

    private Map<String, TokenResponseDto> fetchTokenPrices() {
        try {
            return coinCapClient.getTokensPrices()
                    .onErrorResume(e -> {
                        log.error("Failed to fetch token prices: {}", e.getMessage(), e);
                        return Mono.empty();
                    })
                    .blockOptional()
                    .orElse(Collections.emptyMap());
        } catch (Exception e) {
            log.error("Unexpected error fetching token prices", e);
            return Collections.emptyMap();
        }
    }

    private Callable<Void> createUpdateTask(AssetEntity asset, Map<String, TokenResponseDto> tokenPrices) {
        return () -> {
            try {
                BigDecimal updatedPrice = Optional.ofNullable(tokenPrices.get(asset.getSymbol()))
                        .map(TokenResponseDto::price)
                        .orElse(asset.getPrice());

                asset.setPrice(updatedPrice);
                assetRepository.save(asset);

                log.debug("Updated price for {}: {}", asset.getSymbol(), updatedPrice);
            } catch (Exception e) {
                log.error("Failed to update price for {}: {}", asset.getSymbol(), e.getMessage(), e);
            }
            return null;
        };
    }

    private void executeTasks(List<Callable<Void>> tasks) {
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Price update tasks interrupted", e);
        }
    }
}