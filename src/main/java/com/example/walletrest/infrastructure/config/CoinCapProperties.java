package com.example.walletrest.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "coincap")
@Data
public class CoinCapProperties {
    private String baseUrl;
    private String apiKey;
}