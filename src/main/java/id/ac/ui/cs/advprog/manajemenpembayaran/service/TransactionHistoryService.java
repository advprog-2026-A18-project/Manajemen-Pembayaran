package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.TransactionHistory;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TransactionType;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.TransactionHistoryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionHistoryService {

    private static final String TOP_UP_REFERENCE_TYPE = "TOP_UP_REQUEST";
    private static final String PAYROLL_REFERENCE_TYPE = "PAYROLL";

    private final TransactionHistoryRepository transactionHistoryRepository;

    public TransactionHistoryService(TransactionHistoryRepository transactionHistoryRepository) {
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    public TransactionHistory recordTopUp(String ownerId, BigDecimal amount, Long topUpRequestId) {
        return save(ownerId, TransactionType.TOP_UP, amount, TOP_UP_REFERENCE_TYPE, topUpRequestId);
    }

    public TransactionHistory recordPayrollPayment(String ownerId, BigDecimal amount, Long payrollId) {
        return save(ownerId, TransactionType.PAYROLL_PAYMENT, amount, PAYROLL_REFERENCE_TYPE, payrollId);
    }

    private TransactionHistory save(String ownerId, TransactionType type, BigDecimal amount,
                                    String referenceType, Long referenceId) {
        return transactionHistoryRepository.save(TransactionHistory.builder()
                .ownerId(ownerId)
                .type(type)
                .amount(amount)
                .referenceType(referenceType)
                .referenceId(String.valueOf(referenceId))
                .build());
    }
}
