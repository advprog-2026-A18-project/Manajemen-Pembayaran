package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TransactionHistory;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TransactionType;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TopUpRequest;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TopUpStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.TransactionHistoryRepository;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.TopUpRequestRepository;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class AdminWalletService {

    private static final String ADMIN_WALLET_OWNER_ID = "admin-default";

    private final WalletRepository walletRepository;
    private final TopUpRequestRepository topUpRequestRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    public AdminWalletService(WalletRepository walletRepository, TopUpRequestRepository topUpRequestRepository,
                              TransactionHistoryRepository transactionHistoryRepository) {
        this.walletRepository = walletRepository;
        this.topUpRequestRepository = topUpRequestRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
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

    @Transactional
    public TopUpRequest confirmTopUpRequest(Long topUpRequestId) {
        TopUpRequest request = topUpRequestRepository.findById(topUpRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Top-up request not found for id=" + topUpRequestId));

        if (request.getStatus() != TopUpStatus.PENDING) {
            throw new IllegalStateException("Only PENDING top-up request can be confirmed");
        }

        Wallet adminWallet = walletRepository.findByOwnerId(ADMIN_WALLET_OWNER_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for ownerId=" + ADMIN_WALLET_OWNER_ID));

        adminWallet.setBalance(adminWallet.getBalance().add(request.getAmount()));
        walletRepository.save(adminWallet);

        request.setStatus(TopUpStatus.COMPLETED);
        request.setCompletedAt(LocalDateTime.now());
        TopUpRequest completedRequest = topUpRequestRepository.save(request);

        transactionHistoryRepository.save(TransactionHistory.builder()
                .ownerId(request.getOwnerId())
                .type(TransactionType.TOP_UP)
                .amount(request.getAmount())
                .referenceType("TOP_UP_REQUEST")
                .referenceId(String.valueOf(request.getId()))
                .build());

        return completedRequest;
    }

    public List<TopUpRequest> getTopUpRequests(TopUpStatus status) {
        List<TopUpRequest> requests;
        if (status == null) {
            requests = topUpRequestRepository.findAll();
        } else {
            requests = topUpRequestRepository.findByStatus(status);
        }
        return requests.stream()
                .sorted(Comparator.comparing(TopUpRequest::getCreatedAt).reversed())
                .toList();
    }
}
