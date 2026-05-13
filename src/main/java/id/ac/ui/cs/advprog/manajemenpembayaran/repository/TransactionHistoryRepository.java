package id.ac.ui.cs.advprog.manajemenpembayaran.repository;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
}
