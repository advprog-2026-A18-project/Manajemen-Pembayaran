package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.TopUpRequest;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TopUpStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.AdminWalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
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
    public ResponseEntity<TopUpRequest> topUpAdminWallet(@RequestBody Map<String, BigDecimal> request) {
        return ResponseEntity.ok(adminWalletService.createTopUpRequest(request.get("amount")));
    }

    @GetMapping("/top-up")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<TopUpRequest>> getTopUpRequests(
            @RequestParam(required = false) TopUpStatus status
    ) {
        return ResponseEntity.ok(adminWalletService.getTopUpRequests(status));
    }

    @PostMapping("/top-up/{topUpRequestId}/confirm")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<TopUpRequest> confirmTopUpRequest(@PathVariable Long topUpRequestId) {
        return ResponseEntity.ok(adminWalletService.confirmTopUpRequest(topUpRequestId));
    }
}
