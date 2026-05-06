package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRepository;
import org.springframework.stereotype.Service;

@Service
public class PayrollStatusService {

    private final PayrollRepository payrollRepository;

    public PayrollStatusService(PayrollRepository payrollRepository) {
        this.payrollRepository = payrollRepository;
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
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found for id=" + payrollId));

        if (payroll.getStatus() != PayrollStatus.PENDING) {
            throw new IllegalStateException("Only PENDING payroll can be rejected");
        }

        payroll.setStatus(PayrollStatus.REJECTED);
        payroll.setRejectionReason(rejectionReason);
        return payrollRepository.save(payroll);
    }
}
