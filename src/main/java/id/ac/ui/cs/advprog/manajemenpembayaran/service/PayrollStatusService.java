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

    private final PayrollRepository payrollRepository;
    private final WalletRepository walletRepository;

    public PayrollStatusService(PayrollRepository payrollRepository, WalletRepository walletRepository) {
        this.payrollRepository = payrollRepository;
        this.walletRepository = walletRepository;
    }

    public Payroll acceptPayroll(Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found for id=" + payrollId));

        if (payroll.getStatus() != PayrollStatus.PENDING) {
            throw new IllegalStateException("Only PENDING payroll can be accepted");
        }

        payroll.setStatus(PayrollStatus.ACCEPTED);
        return payrollRepository.save(payroll);
    }

    public Payroll rejectPayroll(Long payrollId, String rejectionReason) {
        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new IllegalArgumentException("rejectionReason is required");
        }

        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found for id=" + payrollId));

        if (payroll.getStatus() != PayrollStatus.PENDING) {
            throw new IllegalStateException("Only PENDING payroll can be rejected");
        }

        payroll.setStatus(PayrollStatus.REJECTED);
        payroll.setRejectionReason(rejectionReason);
        return payrollRepository.save(payroll);
    }

    public Payroll payPayroll(Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found for id=" + payrollId));

        if (payroll.getStatus() != PayrollStatus.ACCEPTED) {
            throw new IllegalStateException("Only ACCEPTED payroll can be paid");
        }

        Wallet wallet = walletRepository.findByOwnerId(payroll.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for ownerId=" + payroll.getOwnerId()));

        wallet.setBalance(wallet.getBalance().add(payroll.getAmount()));
        walletRepository.save(wallet);

        payroll.setStatus(PayrollStatus.PAID);
        return payrollRepository.save(payroll);
    }
}
