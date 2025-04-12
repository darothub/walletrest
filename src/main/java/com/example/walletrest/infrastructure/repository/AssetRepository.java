package com.example.walletrest.infrastructure.repository;

import com.example.walletrest.infrastructure.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {}
