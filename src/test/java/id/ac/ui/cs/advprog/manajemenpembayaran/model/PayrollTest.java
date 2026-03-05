package id.ac.ui.cs.advprog.manajemenpembayaran.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PayrollTest {

    @Test
    void builderAndAccessorsShouldWork() {
        LocalDateTime now = LocalDateTime.now();
        Payroll payroll = Payroll.builder()
                .id(1L)
                .ownerId("buruh-1")
                .ownerRole("BURUH")
                .kilogram(BigDecimal.valueOf(20))
                .amount(BigDecimal.valueOf(36))
                .status(PayrollStatus.ACCEPTED)
                .description("Approved harvest")
                .rejectionReason(null)
                .createdAt(now)
                .build();

        assertEquals(1L, payroll.getId());
        assertEquals("buruh-1", payroll.getOwnerId());
        assertEquals("BURUH", payroll.getOwnerRole());
        assertEquals(BigDecimal.valueOf(20), payroll.getKilogram());
        assertEquals(BigDecimal.valueOf(36), payroll.getAmount());
        assertEquals(PayrollStatus.ACCEPTED, payroll.getStatus());
        assertEquals("Approved harvest", payroll.getDescription());
        assertNull(payroll.getRejectionReason());
        assertEquals(now, payroll.getCreatedAt());
    }

    @Test
    void onCreateShouldSetDefaultStatusAndCreatedAt() {
        Payroll payroll = Payroll.builder()
                .ownerId("buruh-2")
                .ownerRole("BURUH")
                .kilogram(BigDecimal.TEN)
                .amount(BigDecimal.valueOf(18))
                .description("Daily harvest")
                .build();

        assertNull(payroll.getStatus());
        assertNull(payroll.getCreatedAt());

        payroll.onCreate();

        assertEquals(PayrollStatus.PENDING, payroll.getStatus());
        assertNotNull(payroll.getCreatedAt());
    }

    @Test
    void onCreateShouldNotOverrideExistingValues() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Payroll payroll = Payroll.builder()
                .status(PayrollStatus.REJECTED)
                .createdAt(createdAt)
                .build();

        payroll.onCreate();

        assertEquals(PayrollStatus.REJECTED, payroll.getStatus());
        assertEquals(createdAt, payroll.getCreatedAt());
    }
}
