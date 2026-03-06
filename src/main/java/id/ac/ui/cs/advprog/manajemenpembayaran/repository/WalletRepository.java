package id.ac.ui.cs.advprog.manajemenpembayaran.repository;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByOwnerId(String ownerId);
}
