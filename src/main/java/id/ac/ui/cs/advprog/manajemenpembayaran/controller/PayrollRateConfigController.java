package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.dto.PayrollRateRequest;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollRateConfig;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PayrollRateConfigService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pembayaran/admin/rates")
public class PayrollRateConfigController {

    private final PayrollRateConfigService payrollRateConfigService;

    public PayrollRateConfigController(PayrollRateConfigService payrollRateConfigService) {
        this.payrollRateConfigService = payrollRateConfigService;
    }

    @PutMapping
    public ResponseEntity<PayrollRateConfig> upsertRates(@Valid @RequestBody PayrollRateRequest request) {
        return ResponseEntity.ok(payrollRateConfigService.upsert(request));
    }

    @GetMapping
    public ResponseEntity<PayrollRateConfig> getCurrentRate() {
        PayrollRateConfig currentRate = payrollRateConfigService.getCurrentRate();
        if (currentRate == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(currentRate);
    }
}
