package id.ac.ui.cs.advprog.manajemenpembayaran.service;

import id.ac.ui.cs.advprog.manajemenpembayaran.dto.PayrollRateRequest;
import id.ac.ui.cs.advprog.manajemenpembayaran.model.PayrollRateConfig;
import id.ac.ui.cs.advprog.manajemenpembayaran.repository.PayrollRateConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayrollRateConfigServiceTest {

    @Mock
    private PayrollRateConfigRepository payrollRateConfigRepository;

    @InjectMocks
    private PayrollRateConfigService payrollRateConfigService;

    @Test
    void upsertShouldCreateNewConfigWhenNotExists() {
        PayrollRateRequest request = new PayrollRateRequest();
        request.setBuruhRatePerKg(BigDecimal.valueOf(2));
        request.setSupirRatePerKg(BigDecimal.valueOf(3));
        request.setMandorRatePerKg(BigDecimal.valueOf(4));

        when(payrollRateConfigRepository.findTopByOrderByEffectiveFromDesc()).thenReturn(Optional.empty());
        when(payrollRateConfigRepository.save(any(PayrollRateConfig.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PayrollRateConfig result = payrollRateConfigService.upsert(request);

        assertEquals(BigDecimal.valueOf(2), result.getBuruhRatePerKg());
        assertEquals(BigDecimal.valueOf(3), result.getSupirRatePerKg());
        assertEquals(BigDecimal.valueOf(4), result.getMandorRatePerKg());
        assertNotNull(result.getEffectiveFrom());
    }

    @Test
    void upsertShouldUpdateExistingConfig() {
        PayrollRateConfig existing = PayrollRateConfig.builder()
                .id(10L)
                .buruhRatePerKg(BigDecimal.ONE)
                .supirRatePerKg(BigDecimal.ONE)
                .mandorRatePerKg(BigDecimal.ONE)
                .build();

        PayrollRateRequest request = new PayrollRateRequest();
        request.setBuruhRatePerKg(BigDecimal.valueOf(5));
        request.setSupirRatePerKg(BigDecimal.valueOf(6));
        request.setMandorRatePerKg(BigDecimal.valueOf(7));

        when(payrollRateConfigRepository.findTopByOrderByEffectiveFromDesc()).thenReturn(Optional.of(existing));
        when(payrollRateConfigRepository.save(any(PayrollRateConfig.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PayrollRateConfig result = payrollRateConfigService.upsert(request);

        assertEquals(10L, result.getId());
        assertEquals(BigDecimal.valueOf(5), result.getBuruhRatePerKg());
        assertEquals(BigDecimal.valueOf(6), result.getSupirRatePerKg());
        assertEquals(BigDecimal.valueOf(7), result.getMandorRatePerKg());
    }

    @Test
    void getCurrentRateShouldReturnNullWhenNotFound() {
        when(payrollRateConfigRepository.findTopByOrderByEffectiveFromDesc()).thenReturn(Optional.empty());

        PayrollRateConfig result = payrollRateConfigService.getCurrentRate();

        assertNull(result);
    }
}
