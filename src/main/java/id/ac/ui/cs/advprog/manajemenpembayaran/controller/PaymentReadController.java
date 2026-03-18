package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PaymentReadService;

@RestController
@RequestMapping("/api/pembayaran")
public class PaymentReadController {

    private final PaymentReadService paymentReadService;

    public PaymentReadController(PaymentReadService paymentReadService) {
        this.paymentReadService = paymentReadService;
    }

    @GetMapping("/payrolls")
    public ResponseEntity<List<Payroll>> getPayrolls(
            @RequestParam String ownerId,
            @RequestParam(required = false) PayrollStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(paymentReadService.getPayrolls(ownerId, status, startDate, endDate));
    }

    @GetMapping("/wallets/{ownerId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable String ownerId) {
        return ResponseEntity.ok(paymentReadService.getWalletByOwnerId(ownerId));
    }
}
