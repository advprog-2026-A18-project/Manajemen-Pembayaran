package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TransactionHistory;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRepository;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.TransactionHistoryRepository;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentReadService {

    private final PayrollRepository payrollRepository;
    private final WalletRepository walletRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    public PaymentReadService(PayrollRepository payrollRepository, WalletRepository walletRepository,
                              TransactionHistoryRepository transactionHistoryRepository) {
        this.payrollRepository = payrollRepository;
        this.walletRepository = walletRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    public List<Payroll> getPayrolls(String ownerId, PayrollStatus status, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before or equal to endDate");
        }

        boolean hasDateFilter = startDate != null || endDate != null;
        if (!hasDateFilter) {
            if (status == null) {
                return payrollRepository.findByOwnerId(ownerId);
            }
            return payrollRepository.findByOwnerIdAndStatus(ownerId, status);
        }

        LocalDate effectiveStart = startDate != null ? startDate : LocalDate.MIN;
        LocalDate effectiveEnd = endDate != null ? endDate : LocalDate.MAX;
        LocalDateTime from = effectiveStart.atStartOfDay();
        LocalDateTime to = effectiveEnd.plusDays(1).atStartOfDay().minusNanos(1);

        if (status == null) {
            return payrollRepository.findByOwnerIdAndCreatedAtBetween(ownerId, from, to);
        }
        return payrollRepository.findByOwnerIdAndStatusAndCreatedAtBetween(ownerId, status, from, to);
    }

    public List<Payroll> getAllPayrolls(PayrollStatus status, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before or equal to endDate");
        }

        LocalDateTime from = startDate == null ? LocalDate.MIN.atStartOfDay() : startDate.atStartOfDay();
        LocalDateTime to = endDate == null ? LocalDate.MAX.atStartOfDay() : endDate.plusDays(1).atStartOfDay().minusNanos(1);

        return payrollRepository.findAll().stream()
                .filter(payroll -> status == null || payroll.getStatus() == status)
                .filter(payroll -> !payroll.getCreatedAt().isBefore(from) && !payroll.getCreatedAt().isAfter(to))
                .toList();
    }

    public Wallet getWalletByOwnerId(String ownerId) {
        return walletRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for ownerId=" + ownerId));
    }

    public List<TransactionHistory> getTransactionHistory(String ownerId) {
        return transactionHistoryRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
    }
}
