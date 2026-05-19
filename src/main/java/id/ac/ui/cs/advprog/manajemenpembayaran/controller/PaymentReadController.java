package id.ac.ui.cs.advprog.manajemenpembayaran.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.response.PayrollResponse;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.response.TransactionHistoryResponse;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.response.WalletResponse;
import id.ac.ui.cs.advprog.manajemenpembayaran.service.PaymentReadService;

@RestController
@RequestMapping("/api/pembayaran")
public class PaymentReadController {

    private final PaymentReadService paymentReadService;

    public PaymentReadController(PaymentReadService paymentReadService) {
        this.paymentReadService = paymentReadService;
    }

    @GetMapping("/payrolls")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANDOR','SUPIR','BURUH') && @paymentAuthorizationService.canAccessOwner(#ownerId)")
    public ResponseEntity<List<PayrollResponse>> getPayrolls(
            @RequestParam String ownerId,
            @RequestParam(required = false) PayrollStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(paymentReadService.getPayrolls(ownerId, status, startDate, endDate).stream()
                .map(PayrollResponse::from)
                .toList());
    }

    @GetMapping("/wallets/{ownerId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANDOR','SUPIR','BURUH') && @paymentAuthorizationService.canAccessOwner(#ownerId)")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable String ownerId) {
        return ResponseEntity.ok(WalletResponse.from(paymentReadService.getWalletByOwnerId(ownerId)));
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANDOR','SUPIR','BURUH') && @paymentAuthorizationService.canAccessOwner(#ownerId)")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactionHistory(@RequestParam String ownerId) {
        return ResponseEntity.ok(paymentReadService.getTransactionHistory(ownerId).stream()
                .map(TransactionHistoryResponse::from)
                .toList());
    }
}
