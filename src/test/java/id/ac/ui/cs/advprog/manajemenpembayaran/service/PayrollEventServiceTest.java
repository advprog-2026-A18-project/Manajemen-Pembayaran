package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.dto.PayrollCalculationResult;
import id.ac.ui.cs.advprog.manajemenpembayaran.dto.event.HarvestApprovedEvent;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.Payroll;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollRateConfig;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollSourceType;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollEventServiceTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private PayrollRateConfigService payrollRateConfigService;

    @Mock
    private PayrollCalculatorService payrollCalculatorService;

    @InjectMocks
    private PayrollEventService payrollEventService;

    private HarvestApprovedEvent harvestEvent;

    @BeforeEach
    void setUp() {
        harvestEvent = new HarvestApprovedEvent();
        harvestEvent.setEventId("evt-1");
        harvestEvent.setBuruhId("buruh-1");
        harvestEvent.setKilogram(new BigDecimal("100"));
    }

    @Test
    void processHarvestApprovedShouldCreatePayrollWhenEventIsNew() {
        PayrollRateConfig rateConfig = PayrollRateConfig.builder()
                .buruhRatePerKg(new BigDecimal("2000"))
                .build();

        PayrollCalculationResult calcResult = PayrollCalculationResult.builder()
                .kgUsed(new BigDecimal("100"))
                .rateUsed(new BigDecimal("2000"))
                .amount(new BigDecimal("180000.00"))
                .formulaDescription("Upah Buruh = rate * kilogram * 90%")
                .build();

        when(payrollRepository.findByIdempotencyKey("HARVEST_APPROVAL:evt-1")).thenReturn(Optional.empty());
        when(payrollRateConfigService.getCurrentRate()).thenReturn(rateConfig);
        when(payrollCalculatorService.calculateBuruh(new BigDecimal("100"), new BigDecimal("2000")))
                .thenReturn(calcResult);
        when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payroll result = payrollEventService.processHarvestApproved(harvestEvent);

        assertEquals("buruh-1", result.getOwnerId());
        assertEquals("BURUH", result.getOwnerRole());
        assertEquals(PayrollSourceType.HARVEST_APPROVAL, result.getSourceType());
        assertEquals("evt-1", result.getSourceId());
        assertEquals("HARVEST_APPROVAL:evt-1", result.getIdempotencyKey());
        assertEquals(new BigDecimal("180000.00"), result.getAmount());

        ArgumentCaptor<Payroll> captor = ArgumentCaptor.forClass(Payroll.class);
        verify(payrollRepository).save(captor.capture());
        assertEquals("HARVEST_APPROVAL:evt-1", captor.getValue().getIdempotencyKey());
    }

    @Test
    void processHarvestApprovedShouldReturnExistingPayrollWhenDuplicateEvent() {
        Payroll existing = Payroll.builder()
                .id(1L)
                .idempotencyKey("HARVEST_APPROVAL:evt-1")
                .build();

        when(payrollRepository.findByIdempotencyKey("HARVEST_APPROVAL:evt-1")).thenReturn(Optional.of(existing));

        Payroll result = payrollEventService.processHarvestApproved(harvestEvent);

        assertEquals(1L, result.getId());
        verify(payrollRepository, never()).save(any(Payroll.class));
        verify(payrollRateConfigService, never()).getCurrentRate();
    }

    @Test
    void processHarvestApprovedShouldThrowWhenRateConfigMissing() {
        when(payrollRepository.findByIdempotencyKey("HARVEST_APPROVAL:evt-1")).thenReturn(Optional.empty());
        when(payrollRateConfigService.getCurrentRate()).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> payrollEventService.processHarvestApproved(harvestEvent));
    }

    @Test
    void processHarvestApprovedShouldThrowWhenEventInvalid() {
        harvestEvent.setEventId(" ");

        assertThrows(IllegalArgumentException.class, () -> payrollEventService.processHarvestApproved(harvestEvent));
    }
}
