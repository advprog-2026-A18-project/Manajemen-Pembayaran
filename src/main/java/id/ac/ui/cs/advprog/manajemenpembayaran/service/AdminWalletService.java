package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TopUpRequest;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TopUpStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.TopUpRequestRepository;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AdminWalletService {

    private static final String ADMIN_WALLET_OWNER_ID = "admin-default";

    private final WalletRepository walletRepository;
    private final TopUpRequestRepository topUpRequestRepository;

    public AdminWalletService(WalletRepository walletRepository, TopUpRequestRepository topUpRequestRepository) {
        this.walletRepository = walletRepository;
        this.topUpRequestRepository = topUpRequestRepository;
    }

    public Wallet topUpAdminWallet(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("topUpAmount must be greater than 0");
        }

        Wallet adminWallet = walletRepository.findByOwnerId(ADMIN_WALLET_OWNER_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for ownerId=" + ADMIN_WALLET_OWNER_ID));

        adminWallet.setBalance(adminWallet.getBalance().add(amount));
        return walletRepository.save(adminWallet);
    }

    public TopUpRequest createTopUpRequest(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("topUpAmount must be greater than 0");
        }

        TopUpRequest request = TopUpRequest.builder()
                .ownerId(ADMIN_WALLET_OWNER_ID)
                .amount(amount)
                .status(TopUpStatus.PENDING)
                .build();

        return topUpRequestRepository.save(request);
    }

    public TopUpRequest confirmTopUpRequest(Long topUpRequestId) {
        TopUpRequest request = topUpRequestRepository.findById(topUpRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Top-up request not found for id=" + topUpRequestId));
        Wallet adminWallet = walletRepository.findByOwnerId(ADMIN_WALLET_OWNER_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for ownerId=" + ADMIN_WALLET_OWNER_ID));

        adminWallet.setBalance(adminWallet.getBalance().add(request.getAmount()));
        walletRepository.save(adminWallet);

        request.setStatus(TopUpStatus.COMPLETED);
        request.setCompletedAt(LocalDateTime.now());
        return topUpRequestRepository.save(request);
    }
}
