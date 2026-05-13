package id.ac.ui.cs.advprog.manajemenpembayaran.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransactionHistoryTest {

    @Test
    void builderAndAccessorsShouldWork() {
        LocalDateTime createdAt = LocalDateTime.now();

        TransactionHistory transaction = TransactionHistory.builder()
                .id(1L)
                .ownerId("buruh-1")
                .type(TransactionType.PAYROLL_PAYMENT)
                .amount(BigDecimal.valueOf(180000))
                .referenceType("PAYROLL")
                .referenceId("9")
                .createdAt(createdAt)
                .build();

        assertEquals(1L, transaction.getId());
        assertEquals("buruh-1", transaction.getOwnerId());
        assertEquals(TransactionType.PAYROLL_PAYMENT, transaction.getType());
        assertEquals(BigDecimal.valueOf(180000), transaction.getAmount());
        assertEquals("PAYROLL", transaction.getReferenceType());
        assertEquals("9", transaction.getReferenceId());
        assertEquals(createdAt, transaction.getCreatedAt());
    }

    @Test
    void onCreateShouldSetCreatedAt() {
        TransactionHistory transaction = TransactionHistory.builder()
                .ownerId("admin-default")
                .type(TransactionType.TOP_UP)
                .amount(BigDecimal.valueOf(250000))
                .referenceType("TOP_UP_REQUEST")
                .referenceId("6")
                .build();

        assertNull(transaction.getCreatedAt());

        transaction.onCreate();

        assertNotNull(transaction.getCreatedAt());
    }

    @Test
    void onCreateShouldKeepExistingCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        TransactionHistory transaction = TransactionHistory.builder()
                .createdAt(createdAt)
                .build();

        transaction.onCreate();

        assertEquals(createdAt, transaction.getCreatedAt());
    }
}
