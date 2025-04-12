package com.example.walletrest.infrastructure.repository;

import com.example.walletrest.infrastructure.entity.AssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<AssetEntity, Long> {}
