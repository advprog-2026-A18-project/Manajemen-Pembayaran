package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRepository;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.WalletRepository;
import org.springframework.stereotype.Service;

@Service
public class PayrollStatusService {

    private static final String ADMIN_WALLET_OWNER_ID = "admin-default";

    private final PayrollRepository payrollRepository;
    private final WalletRepository walletRepository;

    public PayrollStatusService(PayrollRepository payrollRepository, WalletRepository walletRepository) {
        this.payrollRepository = payrollRepository;
        this.walletRepository = walletRepository;
    }

    public Payroll acceptPayroll(Long payrollId) {
        Payroll payroll = findPayroll(payrollId);
        ensureStatus(payroll, PayrollStatus.PENDING, "Only PENDING payroll can be accepted");

        Wallet workerWallet = findWallet(payroll.getOwnerId());
        Wallet adminWallet = findWallet(ADMIN_WALLET_OWNER_ID);

        workerWallet.setBalance(workerWallet.getBalance().add(payroll.getAmount()));
        adminWallet.setBalance(adminWallet.getBalance().subtract(payroll.getAmount()));
        walletRepository.save(workerWallet);
        walletRepository.save(adminWallet);

        payroll.setStatus(PayrollStatus.ACCEPTED);
        return payrollRepository.save(payroll);
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

    public Payroll payPayroll(Long payrollId) {
        Payroll payroll = findPayroll(payrollId);
        ensureStatus(payroll, PayrollStatus.ACCEPTED, "Only ACCEPTED payroll can be paid");

        Wallet wallet = walletRepository.findByOwnerId(payroll.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for ownerId=" + payroll.getOwnerId()));

        wallet.setBalance(wallet.getBalance().add(payroll.getAmount()));
        walletRepository.save(wallet);

        payroll.setStatus(PayrollStatus.PAID);
        return payrollRepository.save(payroll);
    }

    private Payroll findPayroll(Long payrollId) {
        return payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found for id=" + payrollId));
    }

    private Wallet findWallet(String ownerId) {
        return walletRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for ownerId=" + ownerId));
    }

    private void ensureStatus(Payroll payroll, PayrollStatus expectedStatus, String message) {
        if (payroll.getStatus() != expectedStatus) {
            throw new IllegalStateException(message);
        }
    }
}
