package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Wallet;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRepository;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentReadServiceTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private PaymentReadService paymentReadService;

    @Test
    void getPayrollsShouldReturnAllWhenStatusNull() {
        Payroll payroll = Payroll.builder().ownerId("u1").status(PayrollStatus.PENDING).build();
        when(payrollRepository.findByOwnerId("u1")).thenReturn(List.of(payroll));

        List<Payroll> result = paymentReadService.getPayrolls("u1", null, null, null);

        assertEquals(1, result.size());
        assertEquals("u1", result.get(0).getOwnerId());
    }

    @Test
    void getPayrollsShouldFilterByStatus() {
        Payroll payroll = Payroll.builder().ownerId("u1").status(PayrollStatus.ACCEPTED).build();
        when(payrollRepository.findByOwnerIdAndStatus("u1", PayrollStatus.ACCEPTED)).thenReturn(List.of(payroll));

        List<Payroll> result = paymentReadService.getPayrolls("u1", PayrollStatus.ACCEPTED, null, null);

        assertEquals(1, result.size());
        assertEquals(PayrollStatus.ACCEPTED, result.get(0).getStatus());
    }

    @Test
    void getPayrollsShouldFilterByDateRangeWithoutStatus() {
        Payroll payroll = Payroll.builder().ownerId("u1").build();
        LocalDate start = LocalDate.of(2026, 3, 1);
        LocalDate end = LocalDate.of(2026, 3, 6);
        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to = end.plusDays(1).atStartOfDay().minusNanos(1);

        when(payrollRepository.findByOwnerIdAndCreatedAtBetween("u1", from, to)).thenReturn(List.of(payroll));

        List<Payroll> result = paymentReadService.getPayrolls("u1", null, start, end);

        assertEquals(1, result.size());
        assertEquals("u1", result.get(0).getOwnerId());
    }

    @Test
    void getPayrollsShouldFilterByStatusAndDateRange() {
        Payroll payroll = Payroll.builder().ownerId("u1").status(PayrollStatus.PENDING).build();
        LocalDate start = LocalDate.of(2026, 3, 1);
        LocalDate end = LocalDate.of(2026, 3, 6);
        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to = end.plusDays(1).atStartOfDay().minusNanos(1);

        when(payrollRepository.findByOwnerIdAndStatusAndCreatedAtBetween("u1", PayrollStatus.PENDING, from, to))
                .thenReturn(List.of(payroll));

        List<Payroll> result = paymentReadService.getPayrolls("u1", PayrollStatus.PENDING, start, end);

        assertEquals(1, result.size());
        assertEquals(PayrollStatus.PENDING, result.get(0).getStatus());
    }

    @Test
    void getPayrollsShouldThrowWhenStartDateAfterEndDate() {
        LocalDate start = LocalDate.of(2026, 3, 7);
        LocalDate end = LocalDate.of(2026, 3, 6);

        assertThrows(IllegalArgumentException.class,
                () -> paymentReadService.getPayrolls("u1", null, start, end));
    }

    @Test
    void getWalletByOwnerIdShouldReturnWalletWhenFound() {
        Wallet wallet = Wallet.builder().ownerId("u2").ownerRole("BURUH").balance(BigDecimal.TEN).build();
        when(walletRepository.findByOwnerId("u2")).thenReturn(Optional.of(wallet));

        Wallet result = paymentReadService.getWalletByOwnerId("u2");

        assertEquals("u2", result.getOwnerId());
        assertEquals(BigDecimal.TEN, result.getBalance());
    }

    @Test
    void getWalletByOwnerIdShouldThrowWhenMissing() {
        when(walletRepository.findByOwnerId("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentReadService.getWalletByOwnerId("missing"));
    }
}
