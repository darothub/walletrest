package com.example.walletrest.infrastructure.repository;

import com.example.walletrest.infrastructure.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
