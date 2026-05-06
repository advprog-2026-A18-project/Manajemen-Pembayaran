package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.AdminWalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/pembayaran/admin/wallet")
public class AdminWalletController {

    private final AdminWalletService adminWalletService;

    public AdminWalletController(AdminWalletService adminWalletService) {
        this.adminWalletService = adminWalletService;
    }

    @PostMapping("/top-up")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Wallet> topUpAdminWallet(@RequestBody Map<String, BigDecimal> request) {
        return ResponseEntity.ok(adminWalletService.topUpAdminWallet(request.get("amount")));
    }
}
