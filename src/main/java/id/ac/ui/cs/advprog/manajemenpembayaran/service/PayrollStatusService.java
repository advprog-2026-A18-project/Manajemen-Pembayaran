package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PayrollStatusService {

    private static final String ADMIN_WALLET_OWNER_ID = "admin-default";

    private final PayrollRepository payrollRepository;
    private final WalletTransferService walletTransferService;
    private final TransactionHistoryService transactionHistoryService;

    public PayrollStatusService(PayrollRepository payrollRepository, WalletTransferService walletTransferService,
                                TransactionHistoryService transactionHistoryService) {
        this.payrollRepository = payrollRepository;
        this.walletTransferService = walletTransferService;
        this.transactionHistoryService = transactionHistoryService;
    }

    @Transactional
    public Payroll acceptPayroll(Long payrollId) {
        Payroll payroll = findPayroll(payrollId);
        ensureStatus(payroll, PayrollStatus.PENDING, "Only PENDING payroll can be accepted");

        walletTransferService.transfer(ADMIN_WALLET_OWNER_ID, payroll.getOwnerId(), payroll.getAmount());

        payroll.setStatus(PayrollStatus.ACCEPTED);
        Payroll acceptedPayroll = payrollRepository.save(payroll);

        transactionHistoryService.recordPayrollPayment(payroll.getOwnerId(), payroll.getAmount(), payroll.getId());

        return acceptedPayroll;
    }

    public Payroll rejectPayroll(Long payrollId, String rejectionReason) {
        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new IllegalArgumentException("rejectionReason is required");
        }

        Payroll payroll = findPayroll(payrollId);
        ensureStatus(payroll, PayrollStatus.PENDING, "Only PENDING payroll can be rejected");

        payroll.setStatus(PayrollStatus.REJECTED);
        payroll.setRejectionReason(rejectionReason);
        return payrollRepository.save(payroll);
    }

    @Transactional
    public Payroll payPayroll(Long payrollId) {
        Payroll payroll = findPayroll(payrollId);
        ensureStatus(payroll, PayrollStatus.ACCEPTED, "Only ACCEPTED payroll can be paid");

        walletTransferService.creditWallet(payroll.getOwnerId(), payroll.getAmount());

        payroll.setStatus(PayrollStatus.PAID);
        return payrollRepository.save(payroll);
    }

    private Payroll findPayroll(Long payrollId) {
        return payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found for id=" + payrollId));
    }

    private void ensureStatus(Payroll payroll, PayrollStatus expectedStatus, String message) {
        if (payroll.getStatus() != expectedStatus) {
            throw new IllegalStateException(message);
        }
    }
}
