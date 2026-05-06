package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AdminWalletService {

    private static final String ADMIN_WALLET_OWNER_ID = "admin-default";

    private final WalletRepository walletRepository;

    public AdminWalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet topUpAdminWallet(BigDecimal amount) {
        Wallet adminWallet = walletRepository.findByOwnerId(ADMIN_WALLET_OWNER_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for ownerId=" + ADMIN_WALLET_OWNER_ID));

        adminWallet.setBalance(adminWallet.getBalance().add(amount));
        return walletRepository.save(adminWallet);
    }
}
