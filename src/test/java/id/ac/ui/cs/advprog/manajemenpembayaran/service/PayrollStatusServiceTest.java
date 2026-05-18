package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollStatus;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayrollStatusServiceTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private WalletTransferService walletTransferService;

    @Mock
    private TransactionHistoryService transactionHistoryService;

    @InjectMocks
    private PayrollStatusService payrollStatusService;

    @Test
    void acceptPayrollShouldChangePendingPayrollToAccepted() {
        Payroll payroll = Payroll.builder()
                .id(1L)
                .ownerId("buruh-1")
                .ownerRole("BURUH")
                .kilogram(BigDecimal.valueOf(100))
                .amount(BigDecimal.valueOf(180000))
                .status(PayrollStatus.PENDING)
                .build();
        when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));
        when(payrollRepository.save(payroll)).thenReturn(payroll);

        Payroll acceptedPayroll = payrollStatusService.acceptPayroll(1L);

        ArgumentCaptor<Payroll> payrollCaptor = ArgumentCaptor.forClass(Payroll.class);
        verify(payrollRepository).save(payrollCaptor.capture());
        assertEquals(PayrollStatus.ACCEPTED, acceptedPayroll.getStatus());
        assertEquals(PayrollStatus.ACCEPTED, payrollCaptor.getValue().getStatus());
    }

    @Test
    void rejectPayrollShouldChangePendingPayrollToRejectedAndStoreReason() {
        Payroll payroll = Payroll.builder()
                .id(2L)
                .ownerId("buruh-2")
                .ownerRole("BURUH")
                .kilogram(BigDecimal.valueOf(80))
                .amount(BigDecimal.valueOf(144000))
                .status(PayrollStatus.PENDING)
                .build();

        when(payrollRepository.findById(2L)).thenReturn(Optional.of(payroll));
        when(payrollRepository.save(payroll)).thenReturn(payroll);

        Payroll rejectedPayroll = payrollStatusService.rejectPayroll(2L, "Incorrect harvest data");

        ArgumentCaptor<Payroll> payrollCaptor = ArgumentCaptor.forClass(Payroll.class);
        verify(payrollRepository).save(payrollCaptor.capture());
        assertEquals(PayrollStatus.REJECTED, rejectedPayroll.getStatus());
        assertEquals("Incorrect harvest data", rejectedPayroll.getRejectionReason());
        assertEquals(PayrollStatus.REJECTED, payrollCaptor.getValue().getStatus());
        assertEquals("Incorrect harvest data", payrollCaptor.getValue().getRejectionReason());
    }

    @Test
    void acceptPayrollShouldRejectNonPendingPayroll() {
        Payroll payroll = Payroll.builder()
                .id(3L)
                .ownerId("buruh-3")
                .ownerRole("BURUH")
                .kilogram(BigDecimal.valueOf(60))
                .amount(BigDecimal.valueOf(108000))
                .status(PayrollStatus.REJECTED)
                .build();

        when(payrollRepository.findById(3L)).thenReturn(Optional.of(payroll));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> payrollStatusService.acceptPayroll(3L)
        );

        assertEquals("Only PENDING payroll can be accepted", exception.getMessage());
        verify(payrollRepository, never()).save(payroll);
    }

    @Test
    void rejectPayrollShouldRejectNonPendingPayroll() {
        Payroll payroll = Payroll.builder()
                .id(4L)
                .ownerId("buruh-4")
                .ownerRole("BURUH")
                .kilogram(BigDecimal.valueOf(75))
                .amount(BigDecimal.valueOf(135000))
                .status(PayrollStatus.ACCEPTED)
                .build();

        when(payrollRepository.findById(4L)).thenReturn(Optional.of(payroll));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> payrollStatusService.rejectPayroll(4L, "Incorrect harvest data")
        );

        assertEquals("Only PENDING payroll can be rejected", exception.getMessage());
        verify(payrollRepository, never()).save(payroll);
    }

    @Test
    void payPayrollShouldChangeAcceptedPayrollToPaidAndIncreaseWalletBalance() {
        Payroll payroll = Payroll.builder()
                .id(5L)
                .ownerId("buruh-5")
                .ownerRole("BURUH")
                .kilogram(BigDecimal.valueOf(90))
                .amount(BigDecimal.valueOf(162000))
                .status(PayrollStatus.ACCEPTED)
                .build();
        when(payrollRepository.findById(5L)).thenReturn(Optional.of(payroll));
        when(payrollRepository.save(payroll)).thenReturn(payroll);

        Payroll paidPayroll = payrollStatusService.payPayroll(5L);

        assertEquals(PayrollStatus.PAID, paidPayroll.getStatus());
        verify(walletTransferService).creditWallet("buruh-5", BigDecimal.valueOf(162000));
        verify(payrollRepository).save(payroll);
    }

    @Test
    void rejectPayrollShouldRejectBlankReason() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> payrollStatusService.rejectPayroll(6L, " ")
        );

        assertEquals("rejectionReason is required", exception.getMessage());
        verify(payrollRepository, never()).findById(6L);
        verify(payrollRepository, never()).save(org.mockito.ArgumentMatchers.any(Payroll.class));
    }

    @Test
    void acceptPayrollShouldTransferAmountFromAdminWalletToWorkerWallet() {
        Payroll payroll = Payroll.builder()
                .id(7L)
                .ownerId("buruh-7")
                .ownerRole("BURUH")
                .kilogram(BigDecimal.valueOf(100))
                .amount(BigDecimal.valueOf(180000))
                .status(PayrollStatus.PENDING)
                .build();
        when(payrollRepository.findById(7L)).thenReturn(Optional.of(payroll));
        when(payrollRepository.save(payroll)).thenReturn(payroll);

        Payroll acceptedPayroll = payrollStatusService.acceptPayroll(7L);

        assertEquals(PayrollStatus.ACCEPTED, acceptedPayroll.getStatus());
        verify(walletTransferService).transfer("admin-default", "buruh-7", BigDecimal.valueOf(180000));
    }

    @Test
    void acceptPayrollShouldRecordTransactionHistory() {
        Payroll payroll = Payroll.builder()
                .id(9L)
                .ownerId("buruh-9")
                .ownerRole("BURUH")
                .kilogram(BigDecimal.valueOf(100))
                .amount(BigDecimal.valueOf(180000))
                .status(PayrollStatus.PENDING)
                .build();
        when(payrollRepository.findById(9L)).thenReturn(Optional.of(payroll));
        when(payrollRepository.save(payroll)).thenReturn(payroll);

        payrollStatusService.acceptPayroll(9L);

        verify(transactionHistoryService).recordPayrollPayment("buruh-9", BigDecimal.valueOf(180000), 9L);
    }

    @Test
    void acceptPayrollShouldRejectWhenAdminWalletBalanceIsInsufficient() {
        Payroll payroll = Payroll.builder()
                .id(8L)
                .ownerId("buruh-8")
                .ownerRole("BURUH")
                .kilogram(BigDecimal.valueOf(100))
                .amount(BigDecimal.valueOf(180000))
                .status(PayrollStatus.PENDING)
                .build();
        when(payrollRepository.findById(8L)).thenReturn(Optional.of(payroll));
        org.mockito.Mockito.doThrow(new IllegalStateException("Wallet balance is insufficient"))
                .when(walletTransferService)
                .transfer("admin-default", "buruh-8", BigDecimal.valueOf(180000));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> payrollStatusService.acceptPayroll(8L)
        );

        assertEquals("Wallet balance is insufficient", exception.getMessage());
        verify(walletTransferService).transfer("admin-default", "buruh-8", BigDecimal.valueOf(180000));
        verify(payrollRepository, never()).save(payroll);
    }

    @Test
    void acceptPayrollShouldRunInTransaction() throws NoSuchMethodException {
        Method acceptMethod = PayrollStatusService.class.getMethod("acceptPayroll", Long.class);

        assertTrue(acceptMethod.isAnnotationPresent(Transactional.class));
    }

    @Test
    void payPayrollShouldRunInTransaction() throws NoSuchMethodException {
        Method payMethod = PayrollStatusService.class.getMethod("payPayroll", Long.class);

        assertTrue(payMethod.isAnnotationPresent(Transactional.class));
    }
}
