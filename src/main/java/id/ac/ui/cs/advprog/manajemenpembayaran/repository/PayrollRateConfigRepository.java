package id.ac.ui.cs.advprog.manajemenpembayaran.repository;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollRateConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayrollRateConfigRepository extends JpaRepository<PayrollRateConfig, Long> {
    Optional<PayrollRateConfig> findTopByOrderByEffectiveFromDesc();
}
