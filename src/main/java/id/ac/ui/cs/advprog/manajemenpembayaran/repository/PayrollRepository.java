package id.ac.ui.cs.advprog.manajemenpembayaran.repository;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    List<Payroll> findByOwnerId(String ownerId);

    List<Payroll> findByOwnerIdAndStatus(String ownerId, PayrollStatus status);

    List<Payroll> findByOwnerIdAndCreatedAtBetween(String ownerId, LocalDateTime start, LocalDateTime end);

    List<Payroll> findByOwnerIdAndStatusAndCreatedAtBetween(
            String ownerId,
            PayrollStatus status,
            LocalDateTime start,
            LocalDateTime end
    );
}
