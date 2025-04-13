package com.example.walletrest.application.service;

import com.example.walletrest.application.exception.UserAlreadyExistsException;
import com.example.walletrest.domain.model.Wallet;
import com.example.walletrest.infrastructure.entity.UserEntity;
import com.example.walletrest.infrastructure.entity.WalletEntity;
import com.example.walletrest.infrastructure.repository.UserRepository;
import com.example.walletrest.infrastructure.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceImplTest {

    private UserRepository userRepository;
    private WalletRepository walletRepository;
    private WalletService walletService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        walletRepository = mock(WalletRepository.class);
        walletService = new WalletServiceImpl( userRepository, walletRepository, null, null);
    }

    @Test
    void shouldCreateWalletSuccessfully() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        WalletEntity walletEntity = new WalletEntity();
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setWalletEntity(walletEntity);

        when(userRepository.save(any())).thenReturn(userEntity);

        Wallet result = walletService.createWallet(email);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowWhenUserAlreadyExists() {
        String email = "test@example.com";

        UserEntity existingUser = new UserEntity();
        existingUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class, () -> walletService.createWallet(email));
    }
}