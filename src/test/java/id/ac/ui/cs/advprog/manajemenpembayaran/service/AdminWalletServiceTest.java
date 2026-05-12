package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TopUpRequest;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TopUpStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.TopUpRequestRepository;
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

    @Mock
    private TopUpRequestRepository topUpRequestRepository;

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

    @Test
    void createTopUpRequestShouldCreatePendingRequestWithoutChangingWalletBalance() {
        TopUpRequest savedRequest = TopUpRequest.builder()
                .id(1L)
                .ownerId("admin-default")
                .amount(BigDecimal.valueOf(250000))
                .status(TopUpStatus.PENDING)
                .build();

        when(topUpRequestRepository.save(org.mockito.ArgumentMatchers.any(TopUpRequest.class))).thenReturn(savedRequest);

        TopUpRequest result = adminWalletService.createTopUpRequest(BigDecimal.valueOf(250000));

        assertEquals("admin-default", result.getOwnerId());
        assertEquals(BigDecimal.valueOf(250000), result.getAmount());
        assertEquals(TopUpStatus.PENDING, result.getStatus());
        verify(walletRepository, never()).findByOwnerId("admin-default");
    }
}
