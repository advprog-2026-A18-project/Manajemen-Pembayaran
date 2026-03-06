package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PaymentReadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam(required = false) PayrollStatus status
    ) {
        return ResponseEntity.ok(paymentReadService.getPayrolls(ownerId, status));
    }

    @GetMapping("/wallets/{ownerId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable String ownerId) {
        return ResponseEntity.ok(paymentReadService.getWalletByOwnerId(ownerId));
    }
}
