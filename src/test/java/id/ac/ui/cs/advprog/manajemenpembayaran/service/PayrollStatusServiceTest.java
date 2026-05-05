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

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayrollStatusServiceTest {

    @Mock
    private PayrollRepository payrollRepository;

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
}
