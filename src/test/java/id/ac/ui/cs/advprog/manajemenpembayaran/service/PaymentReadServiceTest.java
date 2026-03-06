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

        List<Payroll> result = paymentReadService.getPayrolls("u1", null);

        assertEquals(1, result.size());
        assertEquals("u1", result.get(0).getOwnerId());
    }

    @Test
    void getPayrollsShouldFilterByStatus() {
        Payroll payroll = Payroll.builder().ownerId("u1").status(PayrollStatus.ACCEPTED).build();
        when(payrollRepository.findByOwnerIdAndStatus("u1", PayrollStatus.ACCEPTED)).thenReturn(List.of(payroll));

        List<Payroll> result = paymentReadService.getPayrolls("u1", PayrollStatus.ACCEPTED);

        assertEquals(1, result.size());
        assertEquals(PayrollStatus.ACCEPTED, result.get(0).getStatus());
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
