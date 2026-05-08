package id.ac.ui.cs.advprog.manajemenpembayaran.service;

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
class AdminWalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private AdminWalletService adminWalletService;

    @Test
    void topUpAdminWalletShouldIncreaseAdminWalletBalance() {
        Wallet adminWallet = Wallet.builder()
                .ownerId("admin-default")
                .ownerRole("ADMIN")
                .balance(BigDecimal.valueOf(100000))
                .build();

        when(walletRepository.findByOwnerId("admin-default")).thenReturn(Optional.of(adminWallet));
        when(walletRepository.save(adminWallet)).thenReturn(adminWallet);

        Wallet updatedWallet = adminWalletService.topUpAdminWallet(BigDecimal.valueOf(250000));

        assertEquals(BigDecimal.valueOf(350000), updatedWallet.getBalance());
        verify(walletRepository).save(adminWallet);
    }

    @Test
    void topUpAdminWalletShouldRejectNonPositiveAmount() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> adminWalletService.topUpAdminWallet(BigDecimal.ZERO)
        );

        assertEquals("topUpAmount must be greater than 0", exception.getMessage());
        verify(walletRepository, never()).findByOwnerId("admin-default");
    }
}
