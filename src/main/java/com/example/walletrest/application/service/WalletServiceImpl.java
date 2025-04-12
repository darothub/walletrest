package com.example.walletrest.application.service;

import com.example.walletrest.application.exception.InvalidAssetPriceException;
import com.example.walletrest.application.exception.ResourceNotFoundException;
import com.example.walletrest.application.exception.UserAlreadyExistsException;
import com.example.walletrest.domain.model.Wallet;
import com.example.walletrest.infrastructure.entity.AssetEntity;
import com.example.walletrest.infrastructure.entity.UserEntity;
import com.example.walletrest.infrastructure.entity.WalletEntity;
import com.example.walletrest.infrastructure.repository.AssetRepository;
import com.example.walletrest.infrastructure.repository.UserRepository;
import com.example.walletrest.infrastructure.repository.WalletRepository;
import com.example.walletrest.infrastructure.client.CoinCapClient;
import com.example.walletrest.presentation.dto.AssetRequestDto;
import com.example.walletrest.presentation.dto.TokenResponseDto;
import com.example.walletrest.presentation.mapper.WalletMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WalletServiceImpl implements WalletService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final AssetRepository assetRepository;
    private final CoinCapClient coinCapClient;

    @Override
    public Wallet createWallet(String email) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        if (userEntity.isPresent()) {
            throw new UserAlreadyExistsException(email);
        }
        UserEntity user = new UserEntity(email);
        WalletEntity wallet = new WalletEntity();
        wallet.setUser(user);
        user.setWalletEntity(wallet);

        WalletEntity savedWalletEntity = userRepository.save(user).getWalletEntity();
        return WalletMapper.toDomain(savedWalletEntity);
    }

    @Override
    public Wallet getWallet(Long id) {
        WalletEntity walletEntity = getWalletEntity(id);
        return WalletMapper.toDomain(walletEntity);
    }

    @Override
    public Wallet addAssetToWallet(Long walletId, AssetRequestDto dto) {
        WalletEntity walletEntity = getWalletEntity(walletId);
        Map<String, TokenResponseDto> tokenAndPriceMap = coinCapClient.getTokensPrices().block();
        if(tokenAndPriceMap != null) {
            var latestPrice = tokenAndPriceMap.get(dto.symbol().toUpperCase(Locale.ROOT)).price();
            if (latestPrice == null || latestPrice.compareTo(dto.price()) != 0) {
                throw new InvalidAssetPriceException();
            }
        }
        AssetEntity assetEntity = WalletMapper.toEntity(dto, dto.price(), walletEntity);
        assetEntity = assetRepository.save(assetEntity);
        walletEntity.getAssetEntities().add(assetEntity);
        return WalletMapper.toDomain(walletEntity);
    }

    private WalletEntity getWalletEntity(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }
}
