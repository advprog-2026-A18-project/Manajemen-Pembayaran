package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRepository;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentReadService {

    private final PayrollRepository payrollRepository;
    private final WalletRepository walletRepository;

    public PaymentReadService(PayrollRepository payrollRepository, WalletRepository walletRepository) {
        this.payrollRepository = payrollRepository;
        this.walletRepository = walletRepository;
    }

    public List<Payroll> getPayrolls(String ownerId, PayrollStatus status) {
        if (status == null) {
            return payrollRepository.findByOwnerId(ownerId);
        }
        return payrollRepository.findByOwnerIdAndStatus(ownerId, status);
    }

    public Wallet getWalletByOwnerId(String ownerId) {
        return walletRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for ownerId=" + ownerId));
    }
}
