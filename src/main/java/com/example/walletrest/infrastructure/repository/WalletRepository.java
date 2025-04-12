package com.example.walletrest.infrastructure.repository;

import com.example.walletrest.infrastructure.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
}
