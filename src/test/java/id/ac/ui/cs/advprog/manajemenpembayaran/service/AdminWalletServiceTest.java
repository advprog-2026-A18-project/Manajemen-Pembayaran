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
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
class AdminWalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TopUpRequestRepository topUpRequestRepository;

    @Mock
    private TransactionHistoryService transactionHistoryService;

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

    @Test
    void confirmTopUpRequestShouldCompleteRequestAndIncreaseAdminWalletBalance() {
        TopUpRequest request = TopUpRequest.builder()
                .id(2L)
                .ownerId("admin-default")
                .amount(BigDecimal.valueOf(250000))
                .status(TopUpStatus.PENDING)
                .build();
        Wallet adminWallet = Wallet.builder()
                .ownerId("admin-default")
                .ownerRole("ADMIN")
                .balance(BigDecimal.valueOf(100000))
                .build();

        when(topUpRequestRepository.findById(2L)).thenReturn(Optional.of(request));
        when(walletRepository.findByOwnerId("admin-default")).thenReturn(Optional.of(adminWallet));
        when(walletRepository.save(adminWallet)).thenReturn(adminWallet);
        when(topUpRequestRepository.save(request)).thenReturn(request);

        TopUpRequest confirmedRequest = adminWalletService.confirmTopUpRequest(2L);

        assertEquals(TopUpStatus.COMPLETED, confirmedRequest.getStatus());
        assertEquals(BigDecimal.valueOf(350000), adminWallet.getBalance());
        verify(walletRepository).save(adminWallet);
        verify(topUpRequestRepository).save(request);
    }

    @Test
    void confirmTopUpRequestShouldRecordTransactionHistory() {
        TopUpRequest request = TopUpRequest.builder()
                .id(6L)
                .ownerId("admin-default")
                .amount(BigDecimal.valueOf(250000))
                .status(TopUpStatus.PENDING)
                .build();
        Wallet adminWallet = Wallet.builder()
                .ownerId("admin-default")
                .ownerRole("ADMIN")
                .balance(BigDecimal.valueOf(100000))
                .build();

        when(topUpRequestRepository.findById(6L)).thenReturn(Optional.of(request));
        when(walletRepository.findByOwnerId("admin-default")).thenReturn(Optional.of(adminWallet));
        when(walletRepository.save(adminWallet)).thenReturn(adminWallet);
        when(topUpRequestRepository.save(request)).thenReturn(request);

        adminWalletService.confirmTopUpRequest(6L);

        verify(transactionHistoryService).recordTopUp("admin-default", BigDecimal.valueOf(250000), 6L);
    }

    @Test
    void confirmTopUpRequestShouldRejectCompletedRequest() {
        TopUpRequest request = TopUpRequest.builder()
                .id(3L)
                .ownerId("admin-default")
                .amount(BigDecimal.valueOf(250000))
                .status(TopUpStatus.COMPLETED)
                .build();

        when(topUpRequestRepository.findById(3L)).thenReturn(Optional.of(request));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> adminWalletService.confirmTopUpRequest(3L)
        );

        assertEquals("Only PENDING top-up request can be confirmed", exception.getMessage());
        verify(walletRepository, never()).findByOwnerId("admin-default");
        verify(topUpRequestRepository, never()).save(request);
    }

    @Test
    void getTopUpRequestsShouldReturnNewestRequestFirst() {
        TopUpRequest olderRequest = TopUpRequest.builder()
                .id(4L)
                .ownerId("admin-default")
                .amount(BigDecimal.valueOf(100000))
                .status(TopUpStatus.PENDING)
                .createdAt(LocalDateTime.of(2026, 5, 1, 10, 0))
                .build();
        TopUpRequest newerRequest = TopUpRequest.builder()
                .id(5L)
                .ownerId("admin-default")
                .amount(BigDecimal.valueOf(200000))
                .status(TopUpStatus.PENDING)
                .createdAt(LocalDateTime.of(2026, 5, 2, 10, 0))
                .build();

        when(topUpRequestRepository.findAll()).thenReturn(List.of(olderRequest, newerRequest));

        List<TopUpRequest> requests = adminWalletService.getTopUpRequests(null);

        assertEquals(List.of(newerRequest, olderRequest), requests);
    }

    @Test
    void confirmTopUpRequestShouldRunInTransaction() throws NoSuchMethodException {
        Method confirmMethod = AdminWalletService.class.getMethod("confirmTopUpRequest", Long.class);

        assertTrue(confirmMethod.isAnnotationPresent(Transactional.class));
    }
}
