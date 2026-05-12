package id.ac.ui.cs.advprog.manajemenpembayaran.repository;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.TopUpRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopUpRequestRepository extends JpaRepository<TopUpRequest, Long> {
}
