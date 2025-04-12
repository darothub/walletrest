package com.example.walletrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WalletrestApplication {
    public static void main(String[] args) {
        SpringApplication.run(WalletrestApplication.class, args);
    }

}
