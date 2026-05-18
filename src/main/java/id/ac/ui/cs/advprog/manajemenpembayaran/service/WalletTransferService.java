package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletTransferService {

    private final WalletRepository walletRepository;

    public WalletTransferService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet creditWallet(String ownerId, BigDecimal amount) {
        Wallet wallet = findWallet(ownerId);
        wallet.setBalance(wallet.getBalance().add(amount));
        return walletRepository.save(wallet);
    }

    public void transfer(String sourceOwnerId, String targetOwnerId, BigDecimal amount) {
        Wallet sourceWallet = findWallet(sourceOwnerId);
        Wallet targetWallet = findWallet(targetOwnerId);

        if (sourceWallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Wallet balance is insufficient");
        }

        sourceWallet.setBalance(sourceWallet.getBalance().subtract(amount));
        targetWallet.setBalance(targetWallet.getBalance().add(amount));
        walletRepository.save(sourceWallet);
        walletRepository.save(targetWallet);
    }

    private Wallet findWallet(String ownerId) {
        return walletRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for ownerId=" + ownerId));
    }
}
