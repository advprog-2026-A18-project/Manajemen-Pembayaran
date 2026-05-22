package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletTransferServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletTransferService walletTransferService;

    @Test
    void creditWalletShouldIncreaseBalance() {
        Wallet wallet = Wallet.builder()
                .ownerId("admin-default")
                .ownerRole("ADMIN")
                .balance(BigDecimal.valueOf(100000))
                .build();

        when(walletRepository.findByOwnerId("admin-default")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);

        Wallet updatedWallet = walletTransferService.creditWallet("admin-default", BigDecimal.valueOf(250000));

        assertEquals(BigDecimal.valueOf(350000), updatedWallet.getBalance());
        verify(walletRepository).save(wallet);
    }

    @Test
    void transferShouldMoveBalanceBetweenWallets() {
        Wallet workerWallet = Wallet.builder()
                .ownerId("buruh-1")
                .ownerRole("BURUH")
                .balance(BigDecimal.valueOf(20000))
                .build();
        Wallet adminWallet = Wallet.builder()
                .ownerId("admin-default")
                .ownerRole("ADMIN")
                .balance(BigDecimal.valueOf(500000))
                .build();

        when(walletRepository.findByOwnerId("admin-default")).thenReturn(Optional.of(adminWallet));
        when(walletRepository.findByOwnerId("buruh-1")).thenReturn(Optional.of(workerWallet));

        walletTransferService.transfer("admin-default", "buruh-1", BigDecimal.valueOf(180000));

        assertEquals(BigDecimal.valueOf(320000), adminWallet.getBalance());
        assertEquals(BigDecimal.valueOf(200000), workerWallet.getBalance());
        verify(walletRepository).save(adminWallet);
        verify(walletRepository).save(workerWallet);
    }

    @Test
    void transferShouldRejectInsufficientBalance() {
        Wallet workerWallet = Wallet.builder()
                .ownerId("buruh-2")
                .ownerRole("BURUH")
                .balance(BigDecimal.ZERO)
                .build();
        Wallet adminWallet = Wallet.builder()
                .ownerId("admin-default")
                .ownerRole("ADMIN")
                .balance(BigDecimal.valueOf(100000))
                .build();

        when(walletRepository.findByOwnerId("admin-default")).thenReturn(Optional.of(adminWallet));
        when(walletRepository.findByOwnerId("buruh-2")).thenReturn(Optional.of(workerWallet));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> walletTransferService.transfer("admin-default", "buruh-2", BigDecimal.valueOf(180000))
        );

        assertEquals("Wallet balance is insufficient", exception.getMessage());
        assertEquals(BigDecimal.valueOf(100000), adminWallet.getBalance());
        assertEquals(BigDecimal.ZERO, workerWallet.getBalance());
        verify(walletRepository, never()).save(adminWallet);
        verify(walletRepository, never()).save(workerWallet);
    }

    @Test
    void creditWalletShouldThrowWhenWalletMissing() {
        when(walletRepository.findByOwnerId("missing")).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> walletTransferService.creditWallet("missing", BigDecimal.valueOf(250000))
        );
    }
}
