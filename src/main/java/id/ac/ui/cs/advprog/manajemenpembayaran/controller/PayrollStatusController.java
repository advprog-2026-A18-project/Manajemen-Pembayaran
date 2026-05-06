package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PayrollStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pembayaran/admin/payrolls")
public class PayrollStatusController {

    private final PayrollStatusService payrollStatusService;

    public PayrollStatusController(PayrollStatusService payrollStatusService) {
        this.payrollStatusService = payrollStatusService;
    }

    @PostMapping("/{payrollId}/accept")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Payroll> acceptPayroll(@PathVariable Long payrollId) {
        return ResponseEntity.ok(payrollStatusService.acceptPayroll(payrollId));
    }

    @PostMapping("/{payrollId}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Payroll> rejectPayroll(
            @PathVariable Long payrollId,
            @RequestBody Map<String, String> request
    ) {
        return ResponseEntity.ok(payrollStatusService.rejectPayroll(payrollId, request.get("rejectionReason")));
    }
}
