package id.ac.ui.cs.advprog.manajemenpembayaran.dto.response;

import id.ac.ui.cs.advprog.manajemenpembayaran.constant.PaymentConstants;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollSourceType;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TransactionHistory;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.TransactionType;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentReadResponseTest {

    @Test
    void payrollResponseShouldMapFromPayrollEntity() {
        LocalDateTime createdAt = LocalDateTime.now();
        Payroll payroll = Payroll.builder()
                .id(1L)
                .ownerId("buruh-1")
                .ownerRole(PaymentConstants.Role.BURUH)
                .kilogram(BigDecimal.valueOf(100))
                .rateUsed(BigDecimal.valueOf(1000))
                .amount(BigDecimal.valueOf(90000))
                .status(PayrollStatus.PENDING)
                .sourceType(PayrollSourceType.HARVEST_APPROVAL)
                .sourceId("harvest-1")
                .description("Upah Buruh")
                .rejectionReason(null)
                .createdAt(createdAt)
                .build();

        PayrollResponse response = PayrollResponse.from(payroll);

        assertEquals(1L, response.getId());
        assertEquals("buruh-1", response.getOwnerId());
        assertEquals(PaymentConstants.Role.BURUH, response.getOwnerRole());
        assertEquals(BigDecimal.valueOf(100), response.getKilogram());
        assertEquals(BigDecimal.valueOf(1000), response.getRateUsed());
        assertEquals(BigDecimal.valueOf(90000), response.getAmount());
        assertEquals(PayrollStatus.PENDING, response.getStatus());
        assertEquals(PayrollSourceType.HARVEST_APPROVAL, response.getSourceType());
        assertEquals("harvest-1", response.getSourceId());
        assertEquals("Upah Buruh", response.getDescription());
        assertEquals(createdAt, response.getCreatedAt());
    }

    @Test
    void walletResponseShouldMapFromWalletEntity() {
        Wallet wallet = Wallet.builder()
                .id(2L)
                .ownerId("buruh-1")
                .ownerRole(PaymentConstants.Role.BURUH)
                .balance(BigDecimal.valueOf(250000))
                .build();

        WalletResponse response = WalletResponse.from(wallet);

        assertEquals("buruh-1", response.getOwnerId());
        assertEquals(PaymentConstants.Role.BURUH, response.getOwnerRole());
        assertEquals(BigDecimal.valueOf(250000), response.getBalance());
    }

    @Test
    void transactionHistoryResponseShouldMapFromTransactionEntity() {
        LocalDateTime createdAt = LocalDateTime.now();
        TransactionHistory transaction = TransactionHistory.builder()
                .id(3L)
                .ownerId("buruh-1")
                .type(TransactionType.PAYROLL_PAYMENT)
                .amount(BigDecimal.valueOf(180000))
                .referenceType(PaymentConstants.Reference.PAYROLL)
                .referenceId("9")
                .createdAt(createdAt)
                .build();

        TransactionHistoryResponse response = TransactionHistoryResponse.from(transaction);

        assertEquals(3L, response.getId());
        assertEquals("buruh-1", response.getOwnerId());
        assertEquals(TransactionType.PAYROLL_PAYMENT, response.getType());
        assertEquals(BigDecimal.valueOf(180000), response.getAmount());
        assertEquals(PaymentConstants.Reference.PAYROLL, response.getReferenceType());
        assertEquals("9", response.getReferenceId());
        assertEquals(createdAt, response.getCreatedAt());
    }
}
